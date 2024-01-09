package com.rdminfo.mms.common.pojo.po;

import com.rdminfo.mms.common.pojo.bo.MusicInfoBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 音乐保存数据
 *
 * @author rdminfo 2023/12/28 11:08
 */
@Data
@Accessors(chain = true)
public class MusicUpdatePO {

    /**
     * 音乐标签是否整理过的
     */
    private List<Item> items;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class Item extends MusicInfoBO {
        private String fileId;
        private String fileName;
        /**
         * 需要更新的字段，前端传入，后端就不必重新获取文件IO或者数据库查找一遍判断是否需要更新
         */
        private List<String> updateColumns;
    }
}

