package com.rdminfo.mms.common.pojo.po;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 音乐歌词保存参数
 *
 * @author rdminfo 2024/01/08 19:50
 */
@Data
@Accessors(chain = true)
public class MusicLyricsEditPO {

    private String lyrics;

}
