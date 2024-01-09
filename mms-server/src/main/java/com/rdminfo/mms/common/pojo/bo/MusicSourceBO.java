package com.rdminfo.mms.common.pojo.bo;

import com.rdminfo.mms.common.entity.MmsMusic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * 音乐文件信息加载BO
 *
 * @author rdminfo 2023/12/05 15:56
 */
@Data
@Accessors(chain = true)
public class MusicSourceBO {

    /**
     * 该音乐所对应的文件表ID
     */
    private Long fileId;

    /**
     * 该音乐保存在数据库的ID
     */
    private Long dbId;

    /**
     * 音乐文件信息MD5用于比较信息是否变更
     */
    private String md5;

    /**
     * 音乐文件File类
     */
    private File file;

    /**
     * 音乐文件类型
     */
    private String fileType;

    /**
     * 音乐文件信息
     */
    private MusicInfoBO musicInfoBO;

    public static MusicSourceBO convert(MmsMusic mmsMusic) {
        MusicSourceBO resBO = new MusicSourceBO();
        if (null == mmsMusic) { return resBO;  }
        resBO.setDbId(mmsMusic.getId()).setFileId(mmsMusic.getFileId()).setMd5(mmsMusic.getMd5());
        return resBO;
    }
}
