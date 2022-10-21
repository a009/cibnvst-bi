package com.vst.dimension.repository;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.vst.dimension.entity.Topic;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author fucheng
 * @date 2022/10/21
 */
@Component
public class TopicRedisRepository extends AbstractBaseRepository<Topic> {

    public TopicRedisRepository(ValueOperations<String, String> valueOperations) {
        super(valueOperations);
    }

    @Override
    public String getRedisKey(Topic topic) {
        return "topic::" + topic.getTopicId();
    }

    @Override
    public String getRedisValue(Topic topic) {
        topic.setLastTime(DateTime.now());
        return JSONUtil.toJsonStr(topic);
    }
}
