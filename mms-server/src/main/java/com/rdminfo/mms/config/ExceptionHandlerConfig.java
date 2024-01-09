package com.rdminfo.mms.config;

import com.rdminfo.mms.common.result.CommonResult;
import com.rdminfo.mms.common.result.CommonResultCode;
import com.rdminfo.mms.common.result.IResultCode;
import com.rdminfo.mms.common.result.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常拦截处理
 *
 * @author rdminfo 2023/12/04 9:19
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfig {

    /** 公共异常 */
    @ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public CommonResult<?> handle(CommonException e, HttpServletRequest request, HttpServletResponse response) {
        IResultCode resultCode = e.getIErrorCode();
        log.error("公共异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), e.getMessage());
        return CommonResult.failed(resultCode);
    }

    /** 参数校验异常(请求参数为Bean但无@RequestBody) */ // get参数类型为Bean post参数类型为Bean
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResult<?> handle(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        String errorText = errorTextHandle(bindingResult.getAllErrors());
        log.error("参数校验异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), errorText);
        return CommonResult.failed(resultCode, errorText);
    }

    /** 参数校验异常(@RequestBody) */ // get参数有ReqBody注解 post参数有ReqBody注解
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResult<?> handle(MethodArgumentNotValidException e) {
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        String errorText = errorTextHandle(e.getBindingResult().getAllErrors());
        log.error("参数校验异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), errorText);
        return CommonResult.failed(resultCode, errorText);
    }

    /** 参数校验异常 */ // get无requestParam get有requestParam post无requestParam post有requestParam
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public CommonResult<?> handle(ConstraintViolationException e) {
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        String errorText = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        log.error("参数校验异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), errorText);
        return CommonResult.failed(resultCode, errorText);
    }

    /** 缺少参数异常(@RequestParam) */ // get有requestParam post有requestParam
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public CommonResult<?> handle(MissingServletRequestParameterException e) {
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        String errorText = "缺少参数：" + e.getParameterName();
        log.error("缺少参数异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), errorText);
        return CommonResult.failed(resultCode, errorText);
    }

    /** 缺少参数异常(@RequestBody) */ // get参数为ReqBody post参数为ReqBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public CommonResult<?> handle(HttpMessageNotReadableException e) {
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        log.error("缺少参数异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), "缺少json格式的参数或json格式错误");
        return CommonResult.failed(resultCode, "缺少json格式的参数或json格式错误");
    }

    /** 传参格式错误异常 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public CommonResult<?> mismatchErrorHandler(MethodArgumentTypeMismatchException e) {
        IResultCode resultCode = CommonResultCode.COMMON_BAD_REQUEST;
        String message = e.getName() + " type mismatch";
        log.error("传参格式错误异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), message);
        return CommonResult.failed(resultCode, message);
    }

    /** 其他异常 */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public CommonResult<?> handle(HttpRequestMethodNotSupportedException e) {
        IResultCode resultCode = CommonResultCode.COMMON_UNKNOWN_HTTP_METHOD;
        log.error("请求方式错误异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), e.getMessage());
        return CommonResult.failed(resultCode);
    }

    /** 非检查异常处理 */
    @ExceptionHandler(value = UndeclaredThrowableException.class)
    @ResponseBody
    public CommonResult<?> handle(UndeclaredThrowableException e) {
        IResultCode resultCode;
        Throwable throwable = e.getCause();
        if (throwable.toString().contains("CommonException")) {
            CommonException commonException = (CommonException) throwable;
            resultCode = commonException.getIErrorCode();
            log.error("非检查异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), e.getMessage());
            return CommonResult.failed(commonException.getIErrorCode());
        } else {
            resultCode = CommonResultCode.COMMON_UNKNOWN_ERROR;
            log.error("未知异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), e.getMessage());
            return CommonResult.failed(resultCode);
        }
    }

    /** 其他异常 */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResult<?> handle(Exception e) {
        IResultCode resultCode = CommonResultCode.COMMON_FAIL;
        log.error("其他异常,code：{}, message: {}, error: {}", resultCode.getCode(), resultCode.getMessage(), e.getMessage());
        return CommonResult.failed(resultCode);
    }

    /** 将参数错误的List转为可读的字符串拼接 */
    private String errorTextHandle(List<ObjectError> errors) {
        StringBuilder errorTxt = new StringBuilder();
        Iterator<ObjectError> iterator = errors.iterator();
        while (iterator.hasNext()) {
            ObjectError error = iterator.next();
            errorTxt.append(error.getDefaultMessage());
            if (iterator.hasNext()) errorTxt.append(", ");
        }
        return errorTxt.toString();
    }

}
