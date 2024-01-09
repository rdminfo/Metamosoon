package com.rdminfo.mms.common.pojo.po;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件夹保存参数
 *
 * @author rdminfo 2023/12/28 11:19
 */
@Data
@Accessors(chain = true)
public class FolderSavePO {
    private String name;
}
