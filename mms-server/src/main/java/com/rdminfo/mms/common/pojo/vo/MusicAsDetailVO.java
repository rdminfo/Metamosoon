package com.rdminfo.mms.common.pojo.vo;

import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 音乐详情返回视图对象
 *
 * @author rdminfo 2024/01/05 21:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MusicAsDetailVO extends MusicInfoBO {

    /**
     * 音乐数据库ID
     */
    private Long id;

    /**
     * 文件数据库ID
     */
    private Long fileId;

}
