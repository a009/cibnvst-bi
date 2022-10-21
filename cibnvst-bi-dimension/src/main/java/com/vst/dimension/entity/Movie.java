package com.vst.dimension.entity;

import cn.hutool.core.date.DateTime;
import lombok.Data;

/**
 * @author fucheng
 * @date 2022/9/30
 */
@Data
public class Movie {
    private String uuid;
    private String title;
    private Integer specialType;
    private Integer cid;
    private String area;
    private Integer year;
    private String director;
    private String cat;
    private String mark;
    private String actor;
    private DateTime lastTime;
}
