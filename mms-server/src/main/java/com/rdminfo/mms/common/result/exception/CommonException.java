package com.rdminfo.mms.common.result.exception;

import com.rdminfo.mms.common.result.IResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;


/**
 * 通用异常的处理
 *
 * @author rdminfo 2023/12/04 11:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;
    private IResultCode iErrorCode;
    private int errorCode;
    private String errorMessage;
    private Map<String, Object> errorData;

    public CommonException(IResultCode iErrorCode) {
        super(iErrorCode.getMessage());
        this.iErrorCode = iErrorCode;
        this.errorCode = iErrorCode.getCode();
    }

    public CommonException(IResultCode iErrorCode, String errorMessage) {
        super(null == errorMessage ? "" : errorMessage);
        this.iErrorCode = iErrorCode;
        this.errorCode = iErrorCode.getCode();
        this.errorMessage = errorMessage;
    }
}