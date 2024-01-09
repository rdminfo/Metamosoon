package com.rdminfo.mms.common.pojo.po;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文件移动请求参数
 *
 * @author rdminfo 2023/12/20 21:11
 */
@Data
public class FileMovePO {

    /**
     * 移动文件夹ID
     */
    @NotEmpty(message = "参数目标文件夹不能为空")
    private String toFolderId;

    /**
     * 移动的文件
     */
    @NotNull(message = "请传入需要移动的文件参数")
    private List<String> fileIds;

}
