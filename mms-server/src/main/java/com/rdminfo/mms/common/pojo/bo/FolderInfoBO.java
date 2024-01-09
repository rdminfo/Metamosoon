package com.rdminfo.mms.common.pojo.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件夹信息BO
 *
 * @author rdminfo 2023/12/12 14:52
 */
@Data
@Accessors(chain = true)
public class FolderInfoBO {

    /**
     * 文件夹ID
     */
    private String folderId;

    /**
     * 文件夹名称
     */
    private String folderName;

}
