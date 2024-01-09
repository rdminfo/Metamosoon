package com.rdminfo.mms.common.constants;

/**
 * 音乐相关常量
 *
 * @author rdminfo 2023/12/13 22:37
 */
public class MmsMusicConstant {

    /**
     * 音乐信息是否整理的，有歌手、专辑、标题就算是已整理
     */
    public static class Organized {
        /**
         * 已整理的
         */
        public static final int YES = 1;
        public static final String YES_STR = "organized";

        /**
         * 未整理的
         */
        public static final int NO = 0;
        public static final String NO_STR = "disorganized";

    }

    /**
     * 音乐标签的列
     */
    public static class Tags {
        /**
         * 标题
         */
        public static final String TITLE = "title";

        /**
         * 艺术家
         */
        public static final String ARTIST = "artist";

        /**
         * 专辑名
         */
        public static final String ALBUM = "album";

        /**
         * 年份
         */
        public static final String YEAR = "year";

        /**
         * 语言
         */
        public static final String LANGUAGE = "language";

        /**
         * 歌词
         */
        public static final String LYRICS = "lyrics";

        /**
         * 轨道号码
         */
        public static final String TRACK_NUMBER = "trackNumber";

        /**
         * 封面
         */
        public static final String COVER = "cover";

        /**
         * 歌手封面
         */
        public static final String COVER_ART = "coverArt";

        /**
         * 文件格式
         */
        public static final String FORMAT = "format";

        /**
         * 文件比特率
         */
        public static final String BITRATE = "bitrate";

        /**
         * 歌曲时长
         */
        public static final String TRACK_LENGTH = "trackLength";

    }

    /**
     * 音乐标签的范围边界
     */
    public static class TagRange {
        /**
         * 基础信息
         */
        public static final String BASIC = "basic";

        /**
         * 其他信息，包括封面歌词，文件大小歌曲时长等
         */
        public static final String OTHER = "other";

    }

    /**
     * 查询的音乐列表的文件范围边界
     */
    public static class FileType {
        /**
         * 默认的，包含文件和音乐文件
         */
        public static final String DEFAULT = "default";

        /**
         * 仅文件夹
         */
        public static final String ONLY_FOLDER = "onlyFolder";

        /**
         * 仅音乐文件
         */
        public static final String ONLY_MUSIC = "onlyMusic";

        /**
         * 仅音乐文件
         */
        public static final String ALL = "all";

    }

    public static class SortCategory {
        /**
         * 按文件名
         */
        public static final String FILE_NAME = "fileName";

        /**
         * 按歌曲名称
         */
        public static final String SONG_NAME = "songName";

        /**
         * 按歌手
         */
        public static final String ARTIST = "artist";

        /**
         * 仅专辑名
         */
        public static final String ALBUM = "album";

        /**
         * 按编号
         */
        public static final String TRACK_NUMBER = "trackNumber";

        /**
         * 按年份
         */
        public static final String YEAR = "year";

        /**
         * 按文件大小
         */
        public static final String SIZE = "size";

        /**
         * 按歌曲时长
         */
        public static final String TRACK_LENGTH = "trackLength";

    }

}
