package com.vst.report.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;


/**
 * @author fucheng
 * @date 2022/10/20
 * 用户事件 - 播放
 * 检索返回的字段
 */
@Document(indexName = "user_events*")
@Data
public class UserEvents {
    /**
     * ES自生成的ID
     */
    private String id;
    /**
     * 埋点被服务器接收的时间
     */
    private String requestTime;

    /**
     * 埋点被服务器识别的IP
     */
    private String clientIP;

    /**
     * 专区类型
     */
    private Integer specId;

    /**
     * 影片分类
     */
    private Integer cid;

    /**
     * 影片唯一ID
     */
    private String nameId;

    /**
     * 影片名称
     */
    private String name;

    /**
     * 影片选集
     */
    private String subName;

    /**
     * 播放时长（ms）
     */
    private Long duration;

    /**
     * 影片所属平台
     */
    private String site;

    /**
     * 影片清晰度
     */
    private String definition;

    /**
     * 产品名称
     */
    private String pkg;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 网络MAC
     */
    private String wlan0Mac;

    /**
     * 物理MAC
     */
    private String eth0Mac;

    /**
     * 版本编码
     */
    private Integer verCode;

    /**
     * 版本名称
     */
    private String verName;

    /**
     * 客户端自身IP
     */
    private String ip;

    /**
     * 用户所在渠道
     */
    private String channel;

    /**
     * 用户所在软件UUID
     */
    private String uuid;
}
