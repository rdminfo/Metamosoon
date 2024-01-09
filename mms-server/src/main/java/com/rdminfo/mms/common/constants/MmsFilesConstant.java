package com.rdminfo.mms.common.constants;

/**
 * 文件相关常量
 *
 * @author rdminfo 2023/12/05 8:30
 */
public class MmsFilesConstant {

    /**
     * 文件所属分区
     */
    public static class FileCategory {
        /**
         * 音乐分区
         */
        public static final String MUSIC = "music";

        /**
         * 电影分区
         */
        public static final String MOVIE = "movie";

    }

    /**
     * 文件层级
     */
    public static class FileLevel {
        /**
         * 文件的根目录
         */
        public static final String ROOT = "0";

        /**
         * 文件的主目录
         */
        public static final String HOME = "1";

    }

    /**
     * 文件类型
     */
    public static class FileType {
        /**
         * 文件夹
         */
        public static final String FOLDER = "folder";

        /**
         * 音乐文件
         */
        public static final String[] MUSIC = new String[]{"mp3", "wav", "flac", "acc", "ogg", "aiff"};

    }

}
