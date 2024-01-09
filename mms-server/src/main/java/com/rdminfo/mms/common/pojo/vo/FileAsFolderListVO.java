package com.rdminfo.mms.common.pojo.vo;

import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.pojo.bo.FolderInfoBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文件夹列表返回视图对象
 *
 * @author rdminfo 2023/12/12 15:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class FileAsFolderListVO extends FolderInfoBO{

    /**
     * 文件层级
     */
    private Integer level;

    public static FileAsFolderListVO convert (MmsFiles mmsFiles) {
        FileAsFolderListVO resVO = new FileAsFolderListVO();
        resVO.setFolderId(String.valueOf(mmsFiles.getId())).setFolderName(mmsFiles.getName());
        resVO.setLevel(mmsFiles.getLevel());
        return resVO;
    }

}
