package com.rdminfo.mms.common.utils;

import cn.hutool.core.util.StrUtil;
import com.rdminfo.mms.common.constants.RegexConstant;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.result.exception.CommonException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数处理工具类
 *
 * @author rdminfo 2024/01/01 18:49
 */
public class ParamUtil {

    public static List<Long> fileIdsCheck(String fileIds) {
        if (StrUtil.isEmpty(fileIds)) {
            throw new CommonException(CommonResultCode.FILE_ID_ILLEGAL);
        }
        List<String> fileIdStrList = Arrays.asList(fileIds.split(","));
        boolean fileIdsLegal = idsLegalJudge(fileIdStrList);
        if (!fileIdsLegal) {
            throw new CommonException(CommonResultCode.FILE_ID_ILLEGAL);
        }
        return fileIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public static List<String> folderNamesCheck(String folderNames) {
        if (StrUtil.isEmpty(folderNames)) {
            throw new CommonException(CommonResultCode.FOLDER_NAME_ILLEGAL);
        }
        List<String> folderNameList = Arrays.asList(folderNames.split(","));
        for (String folderName : folderNameList) {
            if (!folderName.matches(RegexConstant.FILE_NAME)) {
                throw new CommonException(CommonResultCode.FOLDER_NAME_ILLEGAL);
            }

        }
        return folderNameList;
    }

    public static boolean idLegalJudge(String fileId) {
        return StrUtil.isNotEmpty(fileId) && fileId.matches(RegexConstant.INTEGER);
    }

    public static boolean idsLegalJudge(List<String> fileId) {
        if (null == fileId || fileId.size() == 0) { return true; }
        boolean fileLegalJudge = true;
        for (String fileIdTmp : fileId) {
            if (StrUtil.isEmpty(fileIdTmp) || !fileIdTmp.matches(RegexConstant.INTEGER)) {
                fileLegalJudge = false; break;
            }
        }
        return fileLegalJudge;
    }

}
