package com.rdminfo.mms.common.result;

/**
 * 封装API的错误码
 *
 * @author rdminfo 2023/12/04 11:40
 */
public interface IResultCode {
    int getCode();
    String getMessage();
}
