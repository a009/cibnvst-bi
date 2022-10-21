package com.vst.api.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Setter
public class UserEventHeader {
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

    private Map<String,Object> attrs;

    public Long getRequestTime() {
        return requestTime;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getServerName() {
        return serverName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getMethod() {
        return method;
    }

    public String getClientIP() {
        return clientIP;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getContentType() {
        return contentType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAttrs() {
        return attrs;
    }
}