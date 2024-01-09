package com.rdminfo.mms.common.result;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举了一些常用API操作码
 *
 * @author rdminfo 2023/12/04 11:34
 */
public enum CommonResultCode implements IResultCode {

    /** 通用异常 **/
    COMMON_SUCCESS(10000, "操作成功"),
    COMMON_EXCEPTION(10001, "系统异常"),
    COMMON_BAD_REQUEST(10002, "参数有误"),
    COMMON_FAIL(10003, "操作失败，请稍后再试"),
    COMMON_SOURCE_NOTFOUND(10004, "找不到相关资源"),
    COMMON_UNAUTHORIZED(10005, "unauthorized"),
    COMMON_FORBIDDEN(10006, "请求被拒绝了"),
    COMMON_ERROR_CODE(10007, "验证码错误"),
    COMMON_ERROR_CAPTCHA(10008, "图片验证码异常"),
    COMMON_UNKNOWN_ERROR(10009, "未知异常,请联系管理员"),
    COMMON_UNKNOWN_OPE(100010, "未知操作类型"),
    COMMON_UNKNOWN_HTTP_METHOD(10011, "请求方式错误"),

    /** 文件相关 **/
    FILE_NOT_EXIST_IN_SYSTEM(20001, "文件在系统中不存在"),
    FILE_NOT_EXIST_IN_DB(20002, "文件在库中不存在"),
    FILE_ID_ILLEGAL(20003, "文件ID不合法"),
    FILE_UPLOAD_EMPTY(20004, "请选择文件进行上传"),
    FILE_UPLOAD_ERROR_IO(20005, "保存文件时发生IO异常"),
    FILE_TO_MUSIC_INFO_ERROR(20006, "获取音乐标签信息异常"),
    FILE_ZIP_ERROR(20007, "文件打包时失败"),
    FILE_ZIP_ERROR1(20008, "暂不支持跨文件夹打包文件"),
    FILE_ZIP_ERROR2(20009, "打包时失败，父级文件夹出错"),
    FILE_DOWNLOAD_ERROR1(20010, "下载失败，缓存无该文件"),
    FILE_OPE_ERROR1(20010, "无法对根目录文件操作"),
    /** 文件夹相关 **/
    FOLDER_TARGET_NOT_EXIST_IN_SYSTEM(30001, "目标文件夹在系统中不存在"),
    FOLDER_TARGET_NOT_EXIST_IN_DB(30002, "目标文件夹在库中不存在"),
    FOLDER_TARGET_ID_ILLEGAL(30003, "目标文件夹ID不合法"),
    FOLDER_NAME_ILLEGAL(30004, "文件夹名称不合法"),
    /** 音乐相关 **/
    MUSIC_INFO_UPDATE_ERROR(40001, "更新音乐信息文件时发生错误"),
    MUSIC_LYRICS_UPDATE_EMPTY(40002, "更新失败，歌词不能为空"),
    ;

    private int code;
    private String message;

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    CommonResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private static final Map<Integer, CommonResultCode> findMap = new HashMap<>();
    static {
        for (CommonResultCode t : values()) {
            findMap.put(t.getCode(), t);
        }
    }
    public static CommonResultCode findCode(Integer code) {
        return findMap.getOrDefault(code, CommonResultCode.COMMON_UNKNOWN_ERROR);
    }
}
