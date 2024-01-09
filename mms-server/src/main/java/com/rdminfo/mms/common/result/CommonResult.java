package com.rdminfo.mms.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 通用返回对象
 *
 * @author rdminfo 2023/12/04 11:33
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {
    private int code;
    private String message;
    private boolean success;
    private String error;
    private Date time;
    private T data;

    public static <T> CommonResult<T> success() {
        return new CommonResult<>(CommonResultCode.COMMON_SUCCESS.getCode(), CommonResultCode.COMMON_SUCCESS.getMessage(), true, null);
    }

    public static <T> CommonResult<T> success(String message) {
        return new CommonResult<>(CommonResultCode.COMMON_SUCCESS.getCode(), message, true, null);
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(CommonResultCode.COMMON_SUCCESS.getCode(), CommonResultCode.COMMON_SUCCESS.getMessage(), true, data);
    }

    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(CommonResultCode.COMMON_SUCCESS.getCode(), message, true, data);
    }

    public static <T> CommonResult<T> failed(Integer code, String message) {
        return new CommonResult<>(code, message, true, null);
    }

    public static <T> CommonResult<T> failed(IResultCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), false, null);
    }

    public static <T> CommonResult<T> failed(IResultCode errorCode, String error) {
        return new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), false, error, null);
    }

    public static <T> CommonResult<T> failed(int code, String message) {
        return new CommonResult<>(code, message, false, null, null);
    }

    public static <T> CommonResult<T> failed(int code, String message, String error) {
        return new CommonResult<>(code, message, false, error, null);
    }

    protected CommonResult() { }

    protected CommonResult(int code, String message, boolean success, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.time = new Date();
    }

    public CommonResult(int code, String message, boolean success, String error, T data) {
        this.code = code;
        this.message = message;
        this.error = error;
        this.data = data;
        this.success = success;
        this.time = new Date();
    }

    @Override
    public String toString() {
        return "{\"code\": " + this.getCode() + ", \"msg\": " + this.transValue(this.getMessage()) + ", \"data\": " + this.transValue(this.getData()) + "}";
    }

    private String transValue(Object value) {
        return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
    }
}
