package com.rdminfo.mms.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.mapper.MmsMusicMapper;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import com.rdminfo.mms.common.pojo.bo.MusicSourceBO;
import com.rdminfo.mms.common.utils.MusicFileUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 音乐资源Support类
 *
 * @author rdminfo 2023/12/05 22:44
 */
@Slf4j
@Component
public class MusicSourceSupport {

    @Resource
    private MmsMusicMapper musicMapper;

    protected void initMusicSource(Map<Long, MusicSourceBO> musicInitMap) {
        if (musicInitMap.size() == 0) { return; }
        CompletableFuture<Void> loadFilesFuture = CompletableFuture.runAsync(() -> this.loadMusicInfo(musicInitMap));
        List<MusicSourceBO> dbMusicSource = this.loadDbMusicSource();
        loadFilesFuture.join();
        this.doInitMusicSource(musicInitMap, dbMusicSource);
    }

    /**
     * 获取音乐标签信息
     */
    private void loadMusicInfo(Map<Long, MusicSourceBO> musicInitMap) {
        MusicSourceBO tmp; File musicFileTmp; MusicInfoBO musicInfoTmp;
        for (Long key : musicInitMap.keySet()) {
            tmp = musicInitMap.get(key);
            if (!MusicFileUtil.isMusicFile(tmp.getFileType())) { continue; }
            musicFileTmp = tmp.getFile();
            try {
                musicInfoTmp = MusicFileUtil.getMusicInfo(musicFileTmp);
                if (null == musicInfoTmp) { continue; }
            } catch (Exception e) {
                continue;
            }
            tmp.setMusicInfoBO(musicInfoTmp);
        }
    }

    /**
     * 获取数据库里的信息
     */
    private List<MusicSourceBO> loadDbMusicSource() {
        QueryWrapper<MmsMusic> query = new QueryWrapper<>();
        query.select(MmsMusic.ID, MmsMusic.FILE_ID, MmsMusic.MD5);
        return musicMapper.selectList(query).stream().map(MusicSourceBO::convert).collect(Collectors.toList());
    }

    /**
     * 整理数据
     */
    private void doInitMusicSource(Map<Long, MusicSourceBO> musicInitSourceMap, List<MusicSourceBO> dbMusicSource) {
        List<UpdateItem> updateList = new ArrayList<>(); List<Long> removeList = new ArrayList<>();
        Long fileIdTmp; Set<Long> addFileIds = new HashSet<>(musicInitSourceMap.keySet());
        for(MusicSourceBO tmp : dbMusicSource) {
            fileIdTmp = tmp.getFileId();
            if (musicInitSourceMap.containsKey(fileIdTmp)) {
                updateList.add(new UpdateItem(fileIdTmp, tmp.getMd5())); addFileIds.remove(fileIdTmp);
                musicInitSourceMap.get(fileIdTmp).setDbId(tmp.getDbId());
            } else {
                removeList.add(fileIdTmp);
            }
        }
        this.dbInit(musicInitSourceMap, new ArrayList<>(addFileIds), updateList, removeList);
    }

    /**
     * 数据处理，新增的插入到数据库，已存在的更新到数据库，不存在的从数据库删除
     */
    private void dbInit(Map<Long, MusicSourceBO> musicInitSourceMap,
                        List<Long> addList, List<UpdateItem> updateList, List<Long> removeList) {
        MusicSourceBO opeTmp; MusicInfoBO opeMusicInfoTmp; MmsMusic dbOpeTmp;
        removeList.forEach(id -> musicMapper.deleteById(id));
        for (Long addIdTmp : addList) {
            opeTmp = musicInitSourceMap.get(addIdTmp);
            if (null == opeTmp) { continue; }

            opeMusicInfoTmp = opeTmp.getMusicInfoBO();
            dbOpeTmp = MusicInfoBO.reverseConvert(opeMusicInfoTmp);
            dbOpeTmp.setFileId(addIdTmp);
            musicMapper.insert(dbOpeTmp);
        }
        String md5New, md5Old;
        for (UpdateItem updTmp : updateList) {
            opeTmp = musicInitSourceMap.get(updTmp.getFileId());
            if (null == opeTmp) { continue; }
            opeMusicInfoTmp = opeTmp.getMusicInfoBO();
            dbOpeTmp = MusicInfoBO.reverseConvert(opeMusicInfoTmp);
            dbOpeTmp.setId(opeTmp.getDbId()).setFileId(updTmp.getFileId());
            md5Old = updTmp.getMd5(); md5New = dbOpeTmp.getMd5();
            if (null == md5Old || !md5Old.equals(md5New)) { musicMapper.updateById(dbOpeTmp); }
        }
    }

    @Data
    @Accessors(chain = true)
    static class UpdateItem {
        private Long fileId;
        private String md5;

        public UpdateItem(Long fileId, String md5) {
            this.fileId = fileId;
            this.md5 = md5;
        }
    }


}
