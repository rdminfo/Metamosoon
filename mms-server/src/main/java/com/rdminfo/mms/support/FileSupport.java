package com.rdminfo.mms.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rdminfo.mms.common.constants.MmsFilesConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.mapper.MmsFilesMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 文件Support类
 *
 * @author rdminfo 2023/12/20 21:23
 */
@Slf4j
@Component
public class FileSupport {

    @Resource
    private MmsFilesMapper mmsFilesMapper;

    @Value("${mms.music.root-path}")
    private String musicRootPath;
    @Value("${mms.music.tmp-path}")
    private String musicTmpPath;

    public MmsFiles getById(Long fileId, String... columns) {
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(columns.length > 0 ? columns : new String[]{"*"});
        if (Objects.equals(fileId, Long.parseLong(MmsFilesConstant.FileLevel.ROOT))) {
            query.eq(MmsFiles.LEVEL, 0);
        } else {
            query.eq(MmsFiles.ID, fileId);
        }
        query.last(" limit 1");
        return mmsFilesMapper.selectOne(query);
    }

    public List<MmsFiles> getByIds(Collection<Long> fileIds, String... columns) {
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(columns.length > 0 ? columns : new String[]{"*"});
        query.in(MmsFiles.ID, fileIds);
        if (fileIds.contains(Long.parseLong(MmsFilesConstant.FileLevel.ROOT))) {
            query.or(c -> c.eq(MmsFiles.LEVEL, 0));
        }
        return mmsFilesMapper.selectList(query);
    }

    public List<MmsFiles> getParents (Collection<Long> fileIds) {
        return this.getParents(fileIds, true);
    }

    public List<MmsFiles> getParents (Collection<Long> fileIds, boolean includeSelf) {
        RootPathCheckRes checkRes = this.rootPathCheck(fileIds);
        return mmsFilesMapper.selectParentFilesByIds(checkRes.getCheckResList(), checkRes.isFileIdsContainRootId(), includeSelf);
    }

    public List<MmsFiles> getChildren(Collection<Long> fileIds) {
        return this.getChildren(fileIds, true);
    }

    public List<MmsFiles> getChildren(Collection<Long> fileIds, boolean includeSelf) {
        RootPathCheckRes checkRes = this.rootPathCheck(fileIds);
        return mmsFilesMapper.selectChildrenFilesByIds(checkRes.getCheckResList(), checkRes.isFileIdsContainRootId(), includeSelf);
    }

    private RootPathCheckRes rootPathCheck (Collection<Long> fileIds) {
        List<Long> copyList = new ArrayList<>(fileIds);
        long rootFileId = Long.parseLong(MmsFilesConstant.FileLevel.ROOT);
        boolean fileIdsContainRootId = fileIds.contains(rootFileId);
        if (fileIdsContainRootId) copyList.remove(rootFileId);
        return new RootPathCheckRes(fileIdsContainRootId, copyList);
    }

    public String getMusicRootPath() {
        return this.musicRootPath;
    }

    public String getMusicTmpPath() {
        return this.musicTmpPath;
    }

    @Data
    static class RootPathCheckRes {
        private boolean fileIdsContainRootId;
        private List<Long> checkResList;

        public RootPathCheckRes(boolean fileIdsContainRootId, List<Long> checkResList) {
            this.fileIdsContainRootId = fileIdsContainRootId;
            this.checkResList = checkResList;
        }
    }

}
