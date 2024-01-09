package com.rdminfo.mms.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型排序Enum
 *
 * @author rdminfo 2023/12/29 9:25
 */
public enum FileType {

    /**
     * 文件夹
     */
    FOLDER("folder", 1),

    /**
     * mp3文件
     */
    MP3("mp3", 2),

    /**
     * wav文件
     */
    WAV("wav", 2),

    /**
     * flac文件
     */
    FLAC("flac", 2),

    /**
     * 其他类型
     */
    OTHER("other", 2);

    private final String code;
    private final int sort;

    FileType(String code, int sort) {
        this.code = code;
        this.sort = sort;
    }

    public String getCode() {
        return code;
    }

    public int getSort() {
        return sort;
    }

    /**
     * code对应map
     */
    private static final Map<String, FileType> typeMap = new HashMap<>();
    static {
        for (FileType fileType : FileType.values()) {
            typeMap.put(fileType.code, fileType);
        }
    }

    public static FileType find(String code) {
        return typeMap.get(code);
    }
}
