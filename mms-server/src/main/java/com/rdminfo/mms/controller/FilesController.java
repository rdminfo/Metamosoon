package com.rdminfo.mms.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import com.rdminfo.mms.common.constants.MmsFilesConstant;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.pojo.bo.FileSourceBO;
import com.rdminfo.mms.common.pojo.bo.FileZipBO;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import com.rdminfo.mms.common.pojo.po.FileMovePO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderListVO;
import com.rdminfo.mms.common.pojo.vo.FileAsFolderTreeListVO;
import com.rdminfo.mms.common.result.CommonResult;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.result.exception.CommonException;
import com.rdminfo.mms.common.utils.MusicFileUtil;
import com.rdminfo.mms.common.utils.ParamUtil;
import com.rdminfo.mms.service.IMmsFilesService;
import com.rdminfo.mms.service.IMmsMusicService;
import com.rdminfo.mms.support.FileSupport;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件API
 *
 * @author rdminfo 2023/12/07 9:41
 */
@RestController
@RequestMapping("/files")
public class FilesController {

    @Resource
    private IMmsFilesService mmsFilesService;
    @Resource
    private IMmsMusicService mmsMusicService;

    @Resource
    private FileSupport fileSupport;

    @GetMapping("/folders/tree/music")
    public CommonResult<FileAsFolderTreeListVO> foldersAsTreeList() {
        return CommonResult.success(mmsFilesService.getFoldersAsTreeList(MmsFilesConstant.FileCategory.MUSIC));
    }

    @GetMapping("/folders/parents/{fileId}")
    public CommonResult<List<FileAsFolderListVO>> parentFolders(@PathVariable String fileId) {
        if (!ParamUtil.idLegalJudge(fileId)) {
            return CommonResult.success(new ArrayList<>());
        }
        return CommonResult.success(mmsFilesService.getParentFolders(Long.parseLong(fileId)));
    }

    @PutMapping("/move")
    public CommonResult<?> moveFiles(@RequestBody @Valid FileMovePO fileMoveParam) {
        boolean toFolderIdLegal = ParamUtil.idLegalJudge(fileMoveParam.getToFolderId());
        boolean fileIdsLegal = ParamUtil.idsLegalJudge(fileMoveParam.getFileIds());
        if (!toFolderIdLegal) {
            return CommonResult.failed(CommonResultCode.FOLDER_TARGET_ID_ILLEGAL);
        }
        if (!fileIdsLegal) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        Long toFolderId = Long.parseLong(fileMoveParam.getToFolderId());
        List<Long> fileIds = fileMoveParam.getFileIds().stream().map(Long::parseLong).collect(Collectors.toList());
        mmsFilesService.moveFiles(toFolderId, fileIds);
        return CommonResult.success();
    }

    @DeleteMapping("/{fileIds}")
    public CommonResult<?> deleteFiles(@PathVariable String fileIds) {
        List<Long> fileIdList = ParamUtil.fileIdsCheck(fileIds);
        if (fileIdList.contains(Long.parseLong(MmsFilesConstant.FileLevel.ROOT))) {
            throw new CommonException(CommonResultCode.FILE_OPE_ERROR1);
        }
        mmsFilesService.deleteFiles(fileIdList);
        return CommonResult.success();
    }

    @PostMapping("/upload/music/{toFolderId}")
    public CommonResult<?> uploadMusic(@PathVariable String toFolderId, MultipartFile file) {
        if (StrUtil.isEmpty(toFolderId) || !ParamUtil.idLegalJudge(toFolderId)) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        if (file.isEmpty()) {
            return CommonResult.failed(CommonResultCode.FILE_UPLOAD_EMPTY);
        }
        FileSourceBO saveRes = mmsFilesService.saveFile(Long.parseLong(toFolderId), file, MmsFilesConstant.FileCategory.MUSIC);
        MmsMusic saveMusicObj;
        try {
            MusicInfoBO musicInfo = MusicFileUtil.getMusicInfo(saveRes.getFile());
            saveMusicObj = null == musicInfo ? null : MusicInfoBO.reverseConvert(musicInfo);
        } catch (Exception e) {
            return CommonResult.failed(CommonResultCode.FILE_TO_MUSIC_INFO_ERROR);
        }
        if (null == saveMusicObj) {
            return CommonResult.failed(CommonResultCode.FILE_TO_MUSIC_INFO_ERROR);
        }
        saveMusicObj.setFileId(saveRes.getDbId());
        mmsMusicService.save(saveMusicObj);
        return CommonResult.success();
    }

    @PostMapping("/zip/{fileIds}")
    public CommonResult<String> zipFiles(@PathVariable String fileIds) {
        List<Long> fileIdList = ParamUtil.fileIdsCheck(fileIds);
        if (fileIdList.contains(Long.parseLong(MmsFilesConstant.FileLevel.ROOT))) {
            throw new CommonException(CommonResultCode.FILE_OPE_ERROR1);
        }
        FileZipBO zipBO = mmsFilesService.toZipFile(fileIdList);
        if (!zipBO.getSuccess()) { throw new CommonException(CommonResultCode.FILE_ZIP_ERROR); }
        CommonResult<String> commonResult = CommonResult.success();
        commonResult.setData(zipBO.getFileMd5());
        return commonResult;
    }

    @GetMapping("/download/tmp/{md5}")
    public ResponseEntity<FileSystemResource> downloadTmp(@PathVariable String md5) {
        List<String> fileNames = FileUtil.listFileNames(fileSupport.getMusicTmpPath());
        if (fileNames.size() == 0) {
            throw new CommonException(CommonResultCode.COMMON_ERROR_CODE);
        }
        File downloadFile = null; String downloadNamePrefix = null;
        for (String fileName: fileNames) {
            downloadNamePrefix = !fileName.contains("_") ? fileName : fileName.split("_")[0];
            if (fileName.contains(md5)) {
                downloadFile = FileUtil.file(fileSupport.getMusicTmpPath() + "/" + fileName);
            }
        }
        if (null == downloadFile || StrUtil.isEmpty(downloadNamePrefix)) {
            throw new CommonException(CommonResultCode.COMMON_ERROR_CODE);
        }
        String encodedFilename = URLEncodeUtil.encode(downloadNamePrefix + ".zip");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFilename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(downloadFile));
    }

    @PostMapping("/folders/{parentFileId}/{folderNames}")
    public CommonResult<?> addFolder(@PathVariable String parentFileId, @PathVariable String folderNames) {
        if (StrUtil.isEmpty(parentFileId) && !ParamUtil.idLegalJudge(parentFileId)) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        List<String> folderNameList = ParamUtil.folderNamesCheck(folderNames);
        mmsFilesService.addFolder(Long.parseLong(parentFileId), folderNameList);
        return CommonResult.success();
    }

    @PutMapping("/folders/{fileIds}/{folderNames}")
    public CommonResult<?> updFolder(@PathVariable String fileIds, @PathVariable String folderNames) {
        List<Long> fileIdList = ParamUtil.fileIdsCheck(fileIds);
        List<String> folderNameList = ParamUtil.folderNamesCheck(folderNames);
        if (fileIdList.size() != folderNameList.size()) {
            throw new CommonException(CommonResultCode.COMMON_BAD_REQUEST);
        }
        if (fileIdList.contains(Long.parseLong(MmsFilesConstant.FileLevel.ROOT))) {
            throw new CommonException(CommonResultCode.FILE_OPE_ERROR1);
        }
        mmsFilesService.updFileName(fileIdList, folderNameList);
        return CommonResult.success();
    }

}
