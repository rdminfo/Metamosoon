package com.rdminfo.mms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author rdminfo
 * @since 2023-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class MmsMusic implements Serializable {


    /************** SQL字段常量生成 **************/
    public static final String ID = "id";
    public static final String FILE_ID = "file_id";
    public static final String MD5 = "md5";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String YEAR = "year";
    public static final String LANGUAGE = "language";
    public static final String LYRICS = "lyrics";
    public static final String LYRICS_LENGTH = "lyrics_length";
    public static final String TRACK_NUMBER = "track_number";
    public static final String COVER = "cover";
    public static final String COVER_INFO = "cover_info";
    public static final String COVER_ART = "cover_art";
    public static final String FORMAT = "format";
    public static final String BITRATE = "bitrate";
    public static final String TRACK_LENGTH = "track_length";
    public static final String ORGANIZED = "organized";

    private Long id;

    private Long fileId;

    private String md5;

    private String title;

    private String artist;

    private String album;

    private String year;

    private String language;

    private String lyrics;

    private Integer lyricsLength;

    private Integer trackNumber;

    private byte[] cover;

    private String coverInfo;

    private byte[] coverArt;

    private String format;

    private String bitrate;

    private String trackLength;

    private Integer organized;


}
