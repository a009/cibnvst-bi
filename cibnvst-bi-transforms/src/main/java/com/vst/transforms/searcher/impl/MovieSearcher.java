package com.vst.transforms.searcher.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.vst.transforms.searcher.ISearch;
import lombok.AllArgsConstructor;
import com.vst.transforms.searcher.ISearch;

import java.util.Map;
import java.util.Objects;

/**
 * @author fucheng
 * @date 2022/10/1
 */
@AllArgsConstructor
public class MovieSearcher implements AutoCloseable, ISearch {
    private final RedisDS redisDS;

    @Override
    public void close() throws Exception {
        redisDS.close();
    }

    public Map<String, Object> search(String nameId) {
        String jsonStr = redisDS.getStr(getRedisKey(nameId));
        if (!Objects.isNull(jsonStr)) {
            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
            return MapUtil.<String, Object>builder()
                    .put("name", jsonObject.getStr("title"))
                    .put("cid", jsonObject.getInt("cid", 0))
                    .put("specId", jsonObject.getInt("specialType", 0))
                    .build();
        }
        return MapUtil.empty();
    }

    private String getRedisKey(String nameId) {
        return StrUtil.format("movie::{}", nameId);
    }
}
