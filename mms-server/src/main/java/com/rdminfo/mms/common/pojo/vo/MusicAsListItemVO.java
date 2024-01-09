package com.rdminfo.mms.common.pojo.vo;

import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 音乐列表项返回视图对象
 *
 * @author rdminfo 2024/01/01 18:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MusicAsListItemVO extends MusicInfoBO {
    /**
     * 音乐数据库ID
     */
    private String id;
    /**
     * 文件数据库ID
     */
    private String fileId;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件大小
     */
    private Long size;
}
