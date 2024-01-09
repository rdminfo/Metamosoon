package com.rdminfo.mms.common.utils.music.api.netease;

import lombok.Data;

/**
 * NetEase api返回结果
 *
 * @author rdminfo 2023/12/04 10:47
 */
@Data
public class NetEaseResponse<T> {

    /**
     * 成功标志
     */
    private Boolean success;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 返回处理消息
     */
    private String message;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 返回数据对象
     */
    private T result;

}
