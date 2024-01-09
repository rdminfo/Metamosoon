package com.rdminfo.mms.common.utils.music.api;

/**
 * 请求返回结果转译接口
 *
 * @author rdminfo 2023/12/04 14:32
 */
public interface ResponseParser<T> {

    /**
     * 转译
     *
     * @param response 返回数据
     * @return 返回转译后的结果
     */
    T parse(String response);

}
