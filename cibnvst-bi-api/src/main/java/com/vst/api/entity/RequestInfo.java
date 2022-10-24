package com.vst.api.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/23
 * 请求信息封装
 */
@Data
public class RequestInfo {
    /**
     * 请求时间
     */
    private Long requestTime;

    /**
     * 请求完整URL
     */
    private String requestURL;

    /**
     * 请求URI：/action/movie_play
     */
    private String requestURI;

    /**
     * 域名
     */
    private String serverName;

    /**
     * 请求端口
     */
    private Integer serverPort;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 服务端识别IP
     */
    private String clientIP;

    /**
     * UserAgent信息
     */
    private String userAgent;

    /**
     * 请求类型
     */
    private String contentType;

    /**
     * 数据详情
     */
    private Map record;

    @JsonAnyGetter
    public Map getRecord() {
        return record;
    }
}
