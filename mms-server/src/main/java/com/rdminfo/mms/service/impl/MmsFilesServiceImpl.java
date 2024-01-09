package com.rdminfo.mms.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rdminfo.mms.common.constants.MmsFilesConstant;
import com.rdminfo.mms.common.constants.RegexConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.enums.FileType;
import com.rdminfo.mms.common.mapper.MmsFilesMapper;
import com.rdminfo.mms.common.mapper.MmsMusicMapper;
import com.rdminfo.mms.common.pojo.bo.FileSourceBO;
import com.rdminfo.mms.common.pojo.bo.FileZipBO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderListVO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderTreeListVO;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.result.exception.CommonException;
import com.rdminfo.mms.service.IMmsFilesService;
import com.rdminfo.mms.support.FileSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-04
 */
@Service
public class MmsFilesServiceImpl extends ServiceImpl<MmsFilesMapper, MmsFiles> implements IMmsFilesService {
    @Resource
    private MmsMusicMapper musicMapper;

    @Resource
    private FileSupport fileSupport;

    @Override
    public FileAsFolderTreeListVO getFoldersAsTreeList(String fileCategory) {
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(MmsFiles.ID, MmsFiles.PID, MmsFiles.NAME, MmsFiles.LEVEL)
                .eq(MmsFiles.CATEGORY, fileCategory).eq(MmsFiles.TYPE, FileType.FOLDER.getCode())
                .gt(MmsFiles.LEVEL, 0).orderByAsc(MmsFiles.LEVEL).orderByDesc(MmsFiles.LAST_MODIFIED);;
        List<MmsFiles> mmsFileList = this.list(query);
        if (mmsFileList.size() == 0) { return FileAsFolderTreeListVO.empty(); }
        return FileAsFolderTreeListVO.convert(mmsFileList);
    }

    @Override
    public List<FileAsFolderListVO> getParentFolders(Long fileId) {
        return fileSupport.getParents(Collections.singletonList(fileId))
                .stream().map(FileAsFolderListVO::convert).collect(Collectors.toList());
    }

    @Override
    public void moveFiles(Long toFolderId, List<Long> fileIds) {
        List<Long> fileIdsIn = new ArrayList<>();
        fileIdsIn.add(toFolderId); fileIdsIn.addAll(fileIds);
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(MmsFiles.ID, MmsFiles.PID, MmsFiles.NAME, MmsFiles.ABSOLUTE_PATH, MmsFiles.LEVEL)
                .in(MmsFiles.ID, fileIdsIn);
        List<MmsFiles> fileList = this.list(query);

        Map<Long, MmsFiles> filesMap = new HashMap<>(23);
        for (MmsFiles fileTmp : fileList) {
            if (!FileUtil.exist(fileTmp.getAbsolutePath())) {
                throw new CommonException(Objects.equals(toFolderId, fileTmp.getId()) ?
                        CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_SYSTEM : CommonResultCode.FILE_NOT_EXIST_IN_SYSTEM);
            }
            filesMap.put(fileTmp.getId(), fileTmp);
        }
        if (!filesMap.containsKey(toFolderId)) {
            throw new CommonException(CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_DB);
        }

        MmsFiles toFolderDbFile = filesMap.get(toFolderId), moveFileDbTmp;
        String toFolderPath = toFolderDbFile.getAbsolutePath(), rootPath = fileSupport.getMusicRootPath();
        int toFolderLevel = toFolderDbFile.getLevel();
        Map<Long, FileSourceBO> fileUpdMap = new HashMap<>(23);
        File toFileTmp, moveFileTmp, afterMoveFile; String toFilePath;
        for (Long fileId : fileIds) {
            if (!filesMap.containsKey(fileId)) {
                throw new CommonException(CommonResultCode.FILE_NOT_EXIST_IN_DB);
            }
            moveFileDbTmp = filesMap.get(fileId); toFilePath = toFolderPath + "/" + moveFileDbTmp.getName();
            toFileTmp = FileUtil.newFile(toFilePath);
            moveFileTmp = FileUtil.newFile(moveFileDbTmp.getAbsolutePath());
            FileUtil.move(moveFileTmp, toFileTmp, true);
            afterMoveFile = FileUtil.newFile(toFilePath);
            fileUpdMap.put(fileId, new FileSourceBO(rootPath, afterMoveFile, toFolderLevel + 1, fileId, toFolderId));
        }
        // 添加移动目标文件夹和移动当前文件夹以更新信息
        fileUpdMap.put(toFolderId, new FileSourceBO(rootPath, FileUtil.newFile(toFolderPath), toFolderLevel, toFolderId, toFolderDbFile.getPid()));
        this.afterMove(fileUpdMap);
    }

    private void afterMove(Map<Long, FileSourceBO> fileUpdMap) {
        List<FileSourceBO> updListCopy = new ArrayList<>(fileUpdMap.values());
        List<MmsFiles> dbFiles = fileSupport.getChildren(fileUpdMap.keySet(), false);

        FileSourceBO movedFileTmp, movedParentFileTmp;
        String absolutePath, rootPath = fileSupport.getMusicRootPath();
        File fileTmp;
        for (MmsFiles dbFileTmp : dbFiles) {
            movedParentFileTmp = fileUpdMap.get(dbFileTmp.getPid());
            if (movedParentFileTmp == null) {
                continue;
            }
            absolutePath = movedParentFileTmp.getAbsolutePath() + "/" + dbFileTmp.getName();
            if (!FileUtil.exist(absolutePath)) { return; }
            fileTmp = FileUtil.newFile(absolutePath);
            movedFileTmp = new FileSourceBO(rootPath, fileTmp,
                    movedParentFileTmp.getLevel() + 1, dbFileTmp.getId(), movedParentFileTmp.getDbId());
            fileUpdMap.put(dbFileTmp.getId(), movedFileTmp);
            updListCopy.add(movedFileTmp);
        }
        updListCopy.forEach(item -> this.updateById(FileSourceBO.reverseConvert(item)));
    }

    @Override
    public void deleteFiles(List<Long> fileIdList) {
        List<MmsFiles> dbFiles = fileSupport.getChildren(fileIdList, true);
        String delPathTmp; List<Long> delFileId = new ArrayList<>();
        for (MmsFiles fileTmp : dbFiles) {
            delFileId.add(fileTmp.getId());
            delPathTmp = fileTmp.getAbsolutePath();
            if (fileIdList.contains(fileTmp.getId()) && FileUtil.exist(fileTmp.getAbsolutePath())) {
                FileUtil.del(delPathTmp);
            }
        }
        // 删除数据库的数据
        this.removeBatchByIds(delFileId);
        UpdateWrapper<MmsMusic> delCondition = new UpdateWrapper<>();
        delCondition.in(MmsMusic.FILE_ID, delFileId);
        musicMapper.delete(delCondition);
    }

    @Override
    public FileSourceBO saveFile(Long toFolderId, MultipartFile file, String fileCategory) {
        MmsFiles toFolderFile = fileSupport.getById(toFolderId, MmsFiles.ID, MmsFiles.ABSOLUTE_PATH, MmsFiles.LEVEL);
        if (null == toFolderFile) {
            throw new CommonException(CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_DB);
        }

        String toFolderPath = toFolderFile.getAbsolutePath();
        if (StrUtil.isEmpty(toFolderPath) || !FileUtil.exist(toFolderPath)) {
            throw new CommonException(CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_SYSTEM);
        }

        String fileName = StrUtil.cleanBlank(file.getOriginalFilename());
        File saveFile = FileUtil.newFile(toFolderPath + "/" + fileName);
        try {
            FileUtil.writeBytes(file.getBytes(), saveFile);
        } catch (IOException e) {
            throw new CommonException(CommonResultCode.FILE_UPLOAD_ERROR_IO);
        }

        FileSourceBO fileSource = new FileSourceBO(fileSupport.getMusicRootPath(), saveFile,
                toFolderFile.getLevel() + 1);
        fileSource.setDbParentId(toFolderFile.getId());
        MmsFiles saveFileDb = FileSourceBO.reverseConvert(fileSource);
        saveFileDb.setCategory(fileCategory);
        this.save(saveFileDb);
        fileSource.setDbId(saveFileDb.getId());
        return fileSource;
    }

    @Override
    public FileZipBO toZipFile(List<Long> fileIdList) {
        if (null == fileIdList || fileIdList.size() == 0) {
            return new FileZipBO().setSuccess(false);
        }
        QueryWrapper<MmsFiles> query = new QueryWrapper<>();
        query.select(MmsFiles.ID, MmsFiles.PID, MmsFiles.NAME, MmsFiles.ABSOLUTE_PATH, MmsFiles.LEVEL)
                .in(MmsFiles.ID, fileIdList);
        List<MmsFiles> dbFileList = this.list(query); Long parentId = -1L;

        Map<Long, String> downloadFilePathMap = new HashMap<>(23);
        StringBuilder fileIdSb = new StringBuilder();
        for (MmsFiles tmp : dbFileList) {
            if (parentId != -1 && !tmp.getPid().equals(parentId)) {
                throw new CommonException(CommonResultCode.FILE_ZIP_ERROR1);
            }
            parentId = tmp.getPid();
            downloadFilePathMap.put(tmp.getId(), tmp.getAbsolutePath());
            fileIdSb.append(tmp.getId());
        }
        MmsFiles parentFile = this.getById(parentId);
        if (null == parentFile || !FileUtil.exist(parentFile.getAbsolutePath())) {
            throw new CommonException(CommonResultCode.FILE_ZIP_ERROR2);
        }

        String downloadFileMd5 = DigestUtil.md5Hex(fileIdSb.toString());
        String saveFileName = parentFile.getName() + "_" + downloadFileMd5 + ".zip";
        String zipFilePath = fileSupport.getMusicTmpPath() + "/" + saveFileName;
        File zipFile;
        if (FileUtil.exist(zipFilePath)) {
            zipFile = FileUtil.file(zipFilePath);
        } else {
            List<File> downloadFile = new ArrayList<>(); String filePathTmp;
            for (Long fileIdTmp : fileIdList) {
                if (!downloadFilePathMap.containsKey(fileIdTmp)) {
                    throw new CommonException(CommonResultCode.FILE_NOT_EXIST_IN_DB);
                }
                filePathTmp = downloadFilePathMap.get(fileIdTmp);
                if (!FileUtil.exist(filePathTmp)) {
                    throw new CommonException(CommonResultCode.FILE_NOT_EXIST_IN_SYSTEM);
                }
                downloadFile.add(FileUtil.newFile(filePathTmp));
            }
            zipFile = FileUtil.newFile(zipFilePath);
            ZipUtil.zip(zipFile, true, downloadFile.toArray(new File[0]));
        }
        FileZipBO resBO = new FileZipBO();
        resBO.setSuccess(true).setFileMd5(downloadFileMd5).setParentFileName(parentFile.getName()).setZipFile(zipFile);
        return resBO;
    }

    @Override
    public void addFolder(Long parentFileId, List<String> folderNameList) {
        MmsFiles parentDbFile = fileSupport.getById(parentFileId, MmsFiles.ID, MmsFiles.LEVEL, MmsFiles.ABSOLUTE_PATH);
        if (null == parentDbFile) {
            throw new CommonException(CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_DB);
        }
        String parentFolderPath = parentDbFile.getAbsolutePath();
        if (!FileUtil.exist(parentFolderPath)) {
            throw new CommonException(CommonResultCode.FOLDER_TARGET_NOT_EXIST_IN_SYSTEM);
        }

        List<FileSourceBO> addFileSources = new ArrayList<>();
        File addFile; String addFolderPath; FileSourceBO fileSourceTmp;
        for (String folderNameTmp : folderNameList) {
            addFolderPath = parentFolderPath + "/" + folderNameTmp;
            addFile = FileUtil.mkdir(addFolderPath);
            fileSourceTmp = new FileSourceBO(fileSupport.getMusicRootPath(), addFile,
                    parentDbFile.getLevel() + 1);
            fileSourceTmp.setDbParentId(parentDbFile.getId());
            addFileSources.add(fileSourceTmp);
        }
        MmsFiles addDbFileTmp;
        for (FileSourceBO addTmp : addFileSources) {
            addDbFileTmp = FileSourceBO.reverseConvert(addTmp);
            addDbFileTmp.setCategory(MmsFilesConstant.FileCategory.MUSIC);
            this.save(addDbFileTmp);
        }
    }

    @Override
    public void updFileName(List<Long> fileIdList, List<String> fileNameList) {
        if (null == fileIdList || null == fileNameList || fileIdList.size() == 0 || fileIdList.size() != fileNameList.size()) {
            return;
        }
        Map<Long, MmsFiles> updFileMap = fileSupport.getByIds(fileIdList, MmsFiles.ID, MmsFiles.ABSOLUTE_PATH, MmsFiles.LEVEL,
                        MmsFiles.ID, MmsFiles.PID, MmsFiles.NAME)
                .stream().collect(Collectors.toMap(MmsFiles::getId, Function.identity()));

        MmsFiles updDbFile;
        String updFilePath, folderNameTmp; Long fileIdTmp; File renameFile, afterRenameFile; String renameParent;
        FileSourceBO updFileSource; List<FileSourceBO> updFileSourceList = new ArrayList<>();
        for (int i = 0; i < fileIdList.size(); i++) {
            fileIdTmp = fileIdList.get(i); folderNameTmp = fileNameList.get(i);
            updDbFile = updFileMap.get(fileIdTmp);
            updFilePath = null == updDbFile ? null : updDbFile.getAbsolutePath();
            if (null == updDbFile || StrUtil.isEmpty(updFilePath) ||
                    !FileUtil.exist(updFilePath) || !folderNameTmp.matches(RegexConstant.FILE_NAME) ||
                    folderNameTmp.equals(updDbFile.getName())) { continue; }

            renameFile = FileUtil.file(updFilePath); renameParent = renameFile.getParent();
            FileUtil.rename(renameFile, folderNameTmp, true);
            afterRenameFile = FileUtil.file(renameParent + "/" + folderNameTmp);

            updFileSource = new FileSourceBO(fileSupport.getMusicRootPath(), afterRenameFile,
                    updDbFile.getLevel(), updDbFile.getId(), updDbFile.getPid());
            updFileSourceList.add(updFileSource);
        }

        for (FileSourceBO fileSourceTmp : updFileSourceList) {
            updDbFile = FileSourceBO.reverseConvert(fileSourceTmp);
            this.updateById(updDbFile);
        }
    }

}
