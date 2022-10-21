package com.vst.transforms.searcher.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import com.vst.transforms.searcher.ISearch;

import java.util.Map;
import java.util.Objects;

/**
 * @author fucheng
 * @date 2022/10/1
 */
@AllArgsConstructor
public class TopicSearcher implements AutoCloseable, ISearch {
    private final RedisDS redisDS;

    @Override
    public void close() throws Exception {
        redisDS.close();
    }

    public Map<String, Object> search(String topicId) {
        String jsonStr = redisDS.getStr(getRedisKey(topicId));
        if (!Objects.isNull(jsonStr)) {
            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
            return MapUtil.<String, Object>builder()
                    .put("topic", jsonObject.getStr("title"))
                    .put("topicCid", jsonObject.getInt("cid", 0))
                    .put("specId", jsonObject.getInt("specialType", 0))
                    .put("topicType", getTemplateName(jsonObject.getInt("templateType", 0)))
                    .build();
        }
        return MapUtil.empty();
    }

    private String getRedisKey(String topicId) {
        return StrUtil.format("topic::{}", topicId);
    }

    private String getTemplateName(int templateType) {
        switch (templateType) {
            case 1:
                return "影片列表模版";
            case 2:
                return "影片专题模版";
            case 3:
                return "新版少儿专题模版";
            case 4:
                return "事件专题模版";
            case 5:
                return "普通专题";
            case 6:
                return "排行专题";
            case 7:
                return "精选专题";
            case 8:
                return "事件专题";
            case 9:
                return "轮播列表";
            default:
                return "未知";
        }
    }
}
