package com.rdminfo.mms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rdminfo.mms.common.constants.MmsMusicConstant;
import com.rdminfo.mms.common.constants.RegexConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.entity.MmsMusic;
import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import com.rdminfo.mms.common.pojo.po.MusicListQueryPO;
import com.rdminfo.mms.common.pojo.po.MusicLyricsEditPO;
import com.rdminfo.mms.common.pojo.po.MusicUpdatePO;
import com.rdminfo.mms.common.pojo.vo.MusicAsDetailVO;
import com.rdminfo.mms.common.pojo.vo.MusicAsListVO;
import com.rdminfo.mms.common.result.CommonResult;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.utils.ParamUtil;
import com.rdminfo.mms.service.IMmsFilesService;
import com.rdminfo.mms.service.IMmsMusicService;
import com.rdminfo.mms.support.FileSupport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 音乐文件API
 *
 * @author rdminfo 2023/12/04 11:29
 */
@RestController
@RequestMapping("/music")
public class MusicController {

    @Resource
    private IMmsMusicService mmsMusicService;
    @Resource
    private IMmsFilesService mmsFilesService;

    @Resource
    private FileSupport fileSupport;

    @GetMapping("/list/{folderId}")
    public CommonResult<MusicAsListVO> getList(@PathVariable String folderId, MusicListQueryPO queryParam) {
        if (!ParamUtil.idLegalJudge(folderId)) {
            return CommonResult.success(new MusicAsListVO());
        }
        queryParam.parseQueryColumns(); queryParam.parseSortInfo();
        return CommonResult.success(mmsMusicService.getList(Long.parseLong(folderId), queryParam));
    }

    @GetMapping("/{fileId}")
    public CommonResult<MusicAsDetailVO> getDetail(@PathVariable String fileId) {
        if (!ParamUtil.idLegalJudge(fileId)) {
            return CommonResult.success(new MusicAsDetailVO());
        }
        return CommonResult.success(mmsMusicService.getDetail(Long.parseLong(fileId)));
    }

    @GetMapping(value = "/cover/{fileId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] getCover(@PathVariable String fileId) {
        if (!ParamUtil.idLegalJudge(fileId)) {
            return new byte[]{};
        }
        return mmsMusicService.getCover(Long.parseLong(fileId));
    }

    @PostMapping("/cover/{fileId}")
    public CommonResult<?> setCover(@PathVariable String fileId, MultipartFile file) {
        if (StrUtil.isEmpty(fileId) || !ParamUtil.idLegalJudge(fileId)) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        if (file.isEmpty()) {
            return CommonResult.failed(CommonResultCode.FILE_UPLOAD_EMPTY);
        }
        List<MmsFiles> updateFileList = new ArrayList<>();
        updateFileList.add(fileSupport.getById(Long.parseLong(fileId), MmsFiles.ID, MmsFiles.NAME, MmsFiles.ABSOLUTE_PATH));
        List<MusicInfoBO> updateFileInfoList = new ArrayList<>();
        try {
            updateFileInfoList.add(new MusicInfoBO().setCover(file.getBytes()));
        } catch (IOException e) {
            return CommonResult.failed(CommonResultCode.FILE_UPLOAD_ERROR_IO);
        }
        List<Set<String>> updateColumns = new ArrayList<>();
        updateColumns.add(Collections.singleton(MmsMusicConstant.Tags.COVER));
        mmsMusicService.updateInfo(updateFileList, updateFileInfoList, updateColumns);
        return CommonResult.success();
    }

    @DeleteMapping( "/cover/{fileId}")
    public CommonResult<?> delCover(@PathVariable String fileId) {
        if (StrUtil.isEmpty(fileId) || !ParamUtil.idLegalJudge(fileId)) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        List<MmsFiles> updateFileList = new ArrayList<>();
        updateFileList.add(fileSupport.getById(Long.parseLong(fileId), MmsFiles.ID, MmsFiles.NAME, MmsFiles.ABSOLUTE_PATH));
        List<MusicInfoBO> updateFileInfoList = new ArrayList<>();
        updateFileInfoList.add(new MusicInfoBO().setCover(new byte[]{}));
        List<Set<String>> updateColumns = new ArrayList<>();
        updateColumns.add(Collections.singleton(MmsMusicConstant.Tags.COVER));
        mmsMusicService.updateInfo(updateFileList, updateFileInfoList, updateColumns);
        return CommonResult.success();
    }

    @PutMapping( "/lyrics/{fileId}")
    public CommonResult<?> setLyrics(@PathVariable String fileId, @RequestBody MusicLyricsEditPO musicLyricsEditPO) {
        if (StrUtil.isEmpty(fileId) || !ParamUtil.idLegalJudge(fileId)) {
            return CommonResult.failed(CommonResultCode.FILE_ID_ILLEGAL);
        }
        long fileCount = mmsFilesService.count(new QueryWrapper<>(MmsFiles.class).eq(MmsFiles.ID, Long.parseLong(fileId)));
        if (fileCount == 0) {
            return CommonResult.failed(CommonResultCode.FILE_NOT_EXIST_IN_DB);
        }
        String lyrics = StrUtil.isEmpty(musicLyricsEditPO.getLyrics()) ? "" : musicLyricsEditPO.getLyrics();
        UpdateWrapper<MmsMusic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(MmsMusic.LYRICS, lyrics).set(MmsMusic.LYRICS_LENGTH, lyrics.length())
                .eq(MmsMusic.FILE_ID, Long.parseLong(fileId));
        mmsMusicService.update(updateWrapper);
        return CommonResult.success();
    }

    @PutMapping()
    public CommonResult<?> update(@RequestBody @Valid MusicUpdatePO musicUpdatePo) {
        List<MusicUpdatePO.Item> items = musicUpdatePo.getItems();
        List<Long> fileIds = new ArrayList<>();
        for (MusicUpdatePO.Item itemTmp : items) {
            if (!ParamUtil.idLegalJudge(itemTmp.getFileId()) || StrUtil.isEmpty(itemTmp.getFileName()) ||
                    !itemTmp.getFileName().matches(RegexConstant.FILE_NAME)) {
                continue;
            }
            fileIds.add(Long.parseLong(itemTmp.getFileId()));
        }

        Map<Long, MmsFiles> fileMap = fileSupport.getByIds(fileIds, MmsFiles.ID, MmsFiles.NAME, MmsFiles.ABSOLUTE_PATH)
                .stream().collect(Collectors.toMap(MmsFiles::getId, Function.identity()));

        MmsFiles fileTmp; Long fileIdTmp;
        List<Long> fileNameUpdIds = new ArrayList<>(); List<String> fileNames = new ArrayList<>();
        List<MmsFiles> updateFileList = new ArrayList<>();
        List<MusicInfoBO> updateFileInfoList = new ArrayList<>();
        List<Set<String>> updateColumns = new ArrayList<>();
        for (MusicUpdatePO.Item itemTmp : items) {
            fileIdTmp = Long.parseLong(itemTmp.getFileId());
            if (!fileMap.containsKey(fileIdTmp) || null == itemTmp.getUpdateColumns() || itemTmp.getUpdateColumns().size() == 0) {
                continue;
            }
            fileTmp = fileMap.get(fileIdTmp);
            if (!fileTmp.getName().equals(itemTmp.getFileName())){
                fileNameUpdIds.add(fileIdTmp); fileNames.add(itemTmp.getFileName());
            }

            updateFileList.add(fileTmp); updateFileInfoList.add(itemTmp); updateColumns.add(new HashSet<>(itemTmp.getUpdateColumns()));
        }
        if (fileNameUpdIds.size() > 0) {
            mmsFilesService.updFileName(fileNameUpdIds, fileNames);
        }
        if (updateFileList.size() > 0) {
            mmsMusicService.updateInfo(updateFileList, updateFileInfoList, updateColumns);
        }
        return CommonResult.success();
    }

}
