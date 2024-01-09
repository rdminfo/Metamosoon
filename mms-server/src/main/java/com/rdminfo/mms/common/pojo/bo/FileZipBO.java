package com.rdminfo.mms.common.pojo.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * 文件下载BO
 *
 * @author rdminfo 2023/12/27 14:45
 */
@Data
@Accessors(chain = true)
public class FileZipBO {

    /**
     * md5用于校验需要下载的文件是否已经在缓存临时目录存在
     */
    private Boolean success;

    /**
     * md5用于校验需要下载的文件是否已经在缓存临时目录存在
     */
    private String fileMd5;

    /**
     * 下载这些文件的父级文件夹名称
     */
    private String parentFileName;

    /**
     * 压缩的文件
     */
    private File zipFile;

}
