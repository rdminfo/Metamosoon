package com.rdminfo.mms.common.pojo.vo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 音乐列表返回视图对象
 *
 * @author rdminfo 2023/12/07 22:25
 */
@Data
@Accessors(chain = true)
public class MusicAsListVO {

    /**
     * 数据里的歌手列表
     */
    private Set<String> artists;

    /**
     * 数据里的专辑列表
     */
    private Set<String> albums;

    /**
     * 数据里的年份列表
     */
    private Set<String> years;

    /**
     * 数据列表
     */
    private List<MusicAsListItemVO> items;

    public MusicAsListVO() {
        artists = new HashSet<>();
        albums = new HashSet<>();
        years = new HashSet<>();
        items = new ArrayList<>();
    }

    public void addArtist(String artist) {
        if (StrUtil.isEmpty(artist)) {
            return;
        }
        artists.add(artist);
    }

    public void addAlbum(String album) {
        if (StrUtil.isEmpty(album)) {
            return;
        }
        albums.add(album);
    }

    public void addYear(String year) {
        if (StrUtil.isEmpty(year)) {
            return;
        }
        years.add(year);
    }

    public void addItems(List<MusicAsListItemVO> items) {
        this.items.addAll(items);
    }
}
