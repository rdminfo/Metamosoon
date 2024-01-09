package com.rdminfo.mms.common.utils;

import cn.hutool.core.io.unit.DataUnit;

import java.text.DecimalFormat;

/**
 * 文件工具类
 *
 * @author rdminfo 2024/01/04 10:51
 */
public class FileUtil {

    public static String detectImageFormat(byte[] image) {

        final byte[] bmpPrefix  = {(byte)0x42, (byte)0x4D};
        final byte[] jpegPrefix = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
        final byte[] pngPrefix  = {(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47};
        final byte[] gifPrefix  = {(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38};

        final String bmpMimeType  = "bmp";
        final String jpegMimeType = "jpeg";
        final String pngMimeType  = "png";
        final String gifMimeType  = "gif";

        // Check image prefixes
        if(checkImagePrefix(image, bmpPrefix)) { return bmpMimeType; }
        if(checkImagePrefix(image, jpegPrefix)) { return jpegMimeType; }
        if(checkImagePrefix(image, pngPrefix)) { return pngMimeType; }
        if(checkImagePrefix(image, gifPrefix)) { return gifMimeType; }

        // Image type not detected
        return "Unknown";
    }

    private static boolean checkImagePrefix(byte[] image, byte[] prefix) {
        if(image == null || prefix == null) {
            return false;
        }
        boolean match = true;
        for(int i=0; i<prefix.length; i++) {
            if(i < image.length) {
                match &= image[i] == prefix[i];
            } else {
                match = false;
                break;
            }
        }
        return match;
    }

    /**
     * 可读的文件大小
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, true);
    }

    /**
     * 可读的文件大小<br>
     *
     * @param size Long类型大小
     * @param round 是否四舍五入
     * @return 大小
     */
    public static String formatFileSize(long size, boolean round) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = Math.min(DataUnit.UNIT_NAMES.length-1, (int) (Math.log10(size) / Math.log10(1024)));
        return new DecimalFormat(round ? "#,##0" : "#,##0.##").format(size / Math.pow(1024, digitGroups)) + DataUnit.UNIT_NAMES[digitGroups];
    }

}
