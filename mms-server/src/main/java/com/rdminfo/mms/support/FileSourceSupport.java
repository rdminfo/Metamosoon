package com.rdminfo.mms.support;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rdminfo.mms.common.constants.MmsFilesConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.mapper.MmsFilesMapper;
import com.rdminfo.mms.common.pojo.bo.FileSourceBO;
import com.rdminfo.mms.common.pojo.bo.MusicSourceBO;
import com.rdminfo.mms.common.utils.MusicFileUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 文件资源Support类
 *
 * @author rdminfo 2023/12/04 14:20
 */
@Slf4j
@Component
public class FileSourceSupport {

    @Resource
    private MmsFilesMapper mmsFilesMapper;

    @Resource
    private MusicSourceSupport musicSourceSupport;

    @Value("${mms.music.root-path}")
    private String musicRootPath;

    private Map<String, FileSourceBO> musicSourceMap;

    @PostConstruct
    public void initFileSource() {
        musicSourceMap = new HashMap<>(128);
        CompletableFuture<Void> loadMusicFilesFuture = CompletableFuture.runAsync(() -> this.loadFiles(musicRootPath));
        ThreadUtil.execute(() -> {
            List<FileSourceBO> musicDatabaseList = this.loadMusicDataBaseSource();
            loadMusicFilesFuture.join();
            this.doInitMusicFileSource(musicDatabaseList);
        });
    }

    private void loadFiles(String path) {
        File[] files = FileUtil.ls(path);
        // 添加本级path目录
        File[] newFileArray = new File[files.length + 1];
        System.arraycopy(files, 0, newFileArray, 0, files.length);
        newFileArray[newFileArray.length - 1] = new File(path);
        this.loadFiles(newFileArray, 0);
    }

    /**
     * 递归加载文件夹里的所有文件
     */
    private void loadFiles(File[] files, int level) {
        if (null == files || files.length == 0) { return; }
        FileSourceBO musicSourceTmp;
        for (File tmp : files) {
            musicSourceTmp = new FileSourceBO(musicRootPath, tmp, level);
            musicSourceMap.put(tmp.getPath().replace("\\", "/"), musicSourceTmp);
            if (tmp.isDirectory()) {
                int nextLevel = level + 1;
                loadFiles(tmp.listFiles(), nextLevel);
            }
        }
    }

    /**
     * 获取数据库里的文件
     */
    private List<FileSourceBO> loadMusicDataBaseSource() {
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(MmsFiles.ID, MmsFiles.MD5, MmsFiles.ABSOLUTE_PATH);
        return mmsFilesMapper.selectList(new QueryWrapper<>()).stream().map(FileSourceBO::convert).collect(Collectors.toList());
    }

    /**
     * 整合数据
     */
    private void doInitMusicFileSource(List<FileSourceBO> musicDatabaseList) {
        List<UpdateItem> updateList = new ArrayList<>(); List<Long> removeList = new ArrayList<>();
        Set<String> addPath = new HashSet<>(musicSourceMap.keySet()); String dbAbsolutePath;
        for (FileSourceBO tmp : musicDatabaseList) {
            dbAbsolutePath = tmp.getAbsolutePath();
            if (musicSourceMap.containsKey(dbAbsolutePath)) {
                updateList.add(new UpdateItem(dbAbsolutePath, tmp.getMd5())); addPath.remove(dbAbsolutePath);
                musicSourceMap.get(dbAbsolutePath).setDbId(tmp.getDbId());
            } else {
                removeList.add(tmp.getDbId());
            }
        }
        // 根据层级排序，保证由上而下逐级更新。一般用于添加父级后返回父级ID给子级操作
        List<String> addList = addPath.stream().sorted(Comparator.comparing(path -> musicSourceMap.get(path).getLevel())).collect(Collectors.toList());
        updateList = updateList.stream().sorted(Comparator.comparing(item -> musicSourceMap.get(item.getPath()).getLevel())).collect(Collectors.toList());
        this.dbInit(addList, updateList, removeList);
    }

    /**
     * 数据处理，新增的插入到数据库，已存在的更新到数据库，不存在的从数据库删除
     */
    private void dbInit(List<String> addList, List<UpdateItem> updateList, List<Long> removeList) {
        FileSourceBO opeTmp; MmsFiles dbOpeTmp; Long pidTmp;
        MusicSourceBO musicSourceLoadTmp; Map<Long, MusicSourceBO> musicInitMap = new HashMap<>();
        Map<String, Long> idMap = new HashMap<>(128);
        removeList.forEach(id -> mmsFilesMapper.deleteById(id));
        String md5New, md5Old;
        // 先update后新增，update时把id放进map便于add的时候查找父级ID，
        for (UpdateItem updateItemTmp : updateList) {
            opeTmp = musicSourceMap.get(updateItemTmp.getPath());
            if (null == opeTmp) { continue; }

            pidTmp = idMap.getOrDefault(opeTmp.getParentPath(), 0L);

            dbOpeTmp = FileSourceBO.reverseConvert(opeTmp);
            dbOpeTmp.setPid(pidTmp).setCategory(MmsFilesConstant.FileCategory.MUSIC);

            md5Old = updateItemTmp.getMd5(); md5New = dbOpeTmp.getMd5();
            if (!md5Old.equals(md5New)) { mmsFilesMapper.updateById(dbOpeTmp); }

            idMap.put(opeTmp.getPath(), opeTmp.getDbId());
            // 将需要更新或者添加的文件保存到map，已后续加载音乐信息
            if (MusicFileUtil.isMusicFile(dbOpeTmp.getType())) {
                musicSourceLoadTmp = new MusicSourceBO().setFile(opeTmp.getFile()).setFileId(opeTmp.getDbId()).setFileType(dbOpeTmp.getType());
                musicInitMap.put(opeTmp.getDbId(), musicSourceLoadTmp);
            }
        }
        for (String addPath : addList) {
            opeTmp = musicSourceMap.get(addPath);
            if (null == opeTmp) { continue; }

            pidTmp = idMap.getOrDefault(opeTmp.getParentPath(), 0L);

            dbOpeTmp = FileSourceBO.reverseConvert(opeTmp);
            dbOpeTmp.setPid(pidTmp).setCategory(MmsFilesConstant.FileCategory.MUSIC);

            mmsFilesMapper.insert(dbOpeTmp);

            Long id = dbOpeTmp.getId(); idMap.put(opeTmp.getPath(), id);
            // 将需要更新或者添加的文件保存到map，已后续加载音乐信息
            if (MusicFileUtil.isMusicFile(dbOpeTmp.getType())) {
                musicSourceLoadTmp = new MusicSourceBO().setFile(opeTmp.getFile()).setFileId(id).setFileType(dbOpeTmp.getType());
                musicInitMap.put(id, musicSourceLoadTmp);
            }
        }
        musicSourceSupport.initMusicSource(musicInitMap);
    }

    @Data
    @Accessors(chain = true)
    static class UpdateItem {
        private String path;
        private String md5;

        public UpdateItem(String path, String md5) {
            this.path = path;
            this.md5 = md5;
        }
    }

}
