package com.rdminfo.mms.common.pojo.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 音乐信息修改BO
 *
 * @author rdminfo 2023/12/29 13:36
 */
@Data
@Accessors(chain = true)
public class MusicUpdateBO {

    /**
     * 音乐文件ID
     */
    private String fileId;

    /**
     * 需要更新的标签
     */
    private Set<String> updateTags;

    /**
     * 需要更新音乐标签信息
     */
    private MusicInfoBO updateInfo;

}
