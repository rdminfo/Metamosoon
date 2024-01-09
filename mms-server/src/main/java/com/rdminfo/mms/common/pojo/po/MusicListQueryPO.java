package com.rdminfo.mms.common.pojo.po;

import cn.hutool.core.util.StrUtil;
import com.rdminfo.mms.common.constants.CommonConstant;
import com.rdminfo.mms.common.constants.MmsMusicConstant;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.entity.MmsMusic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 音乐列表请求参数
 *
 * @author rdminfo 2023/12/14 22:48
 */
@Data
@Accessors(chain = true)
public class MusicListQueryPO {

    /**
     * 音乐标签是否整理过的
     */
    private String organized;

    /**
     * 是否有专辑名
     */
    private String hasAlbum;

    /**
     * 是否有封面
     */
    private String hasCover;

    /**
     * 是否有歌词
     */
    private String hasLyric;

    /**
     * 是否有年份
     */
    private String hasYear;

    /**
     * 标签范围
     */
    private String tagRange = MmsMusicConstant.TagRange.BASIC;

    /**
     * 标签范围
     */
    private String fileType = MmsMusicConstant.FileType.DEFAULT;

    /**
     * 排序类型
     */
    private String sortCategory;

    /**
     * 排序方式
     */
    private String sortOrder;

    /**
     * 按照歌手筛选
     */
    private String artist;

    /**
     * 按照专辑筛选
     */
    private String album;

    /**
     * 按照年份筛选
     */
    private String year;

    /**
     **********************************
     * 后端生成，前端传入无效
     ***********************************
     */
    private List<QueryColumns> musicQueryColumns;

    private String fileSortColumn;

    private String musicSortColumn;

    private String sort;

    public void parseQueryColumns() {
        List<QueryColumns> musicQueryColumns = new ArrayList<>(Arrays.asList(new QueryColumns(MmsMusic.ID, "id"), new QueryColumns(MmsMusic.ORGANIZED, "organized")));
        if (Objects.equals(this.tagRange, MmsMusicConstant.TagRange.BASIC)) {
            musicQueryColumns.add(new QueryColumns(MmsMusic.TITLE, "title"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.ARTIST, "artist"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.ALBUM, "album"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.YEAR, "year"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.TRACK_NUMBER, "trackNumber"));
        } else {
            musicQueryColumns.add(new QueryColumns(MmsMusic.COVER_INFO, "coverInfo"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.LYRICS, "lyrics"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.LYRICS_LENGTH, "lyricsLength"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.FORMAT, "format"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.BITRATE, "bitrate"));
            musicQueryColumns.add(new QueryColumns(MmsMusic.TRACK_LENGTH, "trackLength"));
        }
        this.musicQueryColumns = musicQueryColumns;
    }

    public void parseSortInfo() {
        if (StrUtil.isEmpty(sortCategory)) { return; }
        switch(sortCategory) {
            case MmsMusicConstant.SortCategory.FILE_NAME:
                fileSortColumn = MmsFiles.NAME;
                break;
            case MmsMusicConstant.SortCategory.SONG_NAME:
                musicSortColumn = MmsMusic.TITLE;
                break;
            case MmsMusicConstant.SortCategory.ARTIST:
                musicSortColumn = MmsMusic.ARTIST;
                break;
            case MmsMusicConstant.SortCategory.ALBUM:
                musicSortColumn = MmsMusic.ALBUM;
                break;
            case MmsMusicConstant.SortCategory.TRACK_NUMBER:
                musicSortColumn = MmsMusic.TRACK_NUMBER;
                break;
            case MmsMusicConstant.SortCategory.YEAR:
                musicSortColumn = MmsMusic.YEAR;
                break;
            case MmsMusicConstant.SortCategory.SIZE:
                fileSortColumn = MmsFiles.SIZE;
                break;
            case MmsMusicConstant.SortCategory.TRACK_LENGTH:
                musicSortColumn = MmsMusic.TRACK_LENGTH;
                break;
            default:
                fileSortColumn = MmsFiles.LAST_MODIFIED;
                break;
        }
        if (StrUtil.isEmpty(sortOrder)) {
            sort = CommonConstant.SORT_ORDER.ASC;
            return;
        }
        sort = Objects.equals(sortOrder, CommonConstant.SORT_ORDER.ASC) ?
                CommonConstant.SORT_ORDER.ASC : CommonConstant.SORT_ORDER.DESC;
    }

    @Data
    static class QueryColumns {
        private String columnName;
        private String columnAlias;

        public QueryColumns(String columnName, String columnAlias) {
            this.columnName = columnName;
            this.columnAlias = columnAlias;
        }
    }

}
