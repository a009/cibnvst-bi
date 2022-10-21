package com.vst.dimension.entity;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author fucheng
 * @date 2022/9/30
 */
@Data
public class Topic {
    private String topicId;
    private String title;
    private Integer specialType;
    private Integer cid;
    private Integer templateType;
    private DateTime lastTime;
}
