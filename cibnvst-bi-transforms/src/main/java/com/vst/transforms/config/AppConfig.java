package com.vst.transforms.config;

import cn.hutool.core.util.StrUtil;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.List;

/**
 * @author fucheng
 * @date 2022/10/11
 */
public class AppConfig {
    public static final String IP_PATH_KEY = "ipPath";
    public static final String REDIS_CONFIG_KEY = "redisConfig";
    public static final String DB_CONFIG_KEY = "dbConfig";
    public static final String CHECKPOINT_INTERVAL_KEY = "checkpointInterval";

    public static final String KAFKA_GROUP_ID_KEY = "groupId";
    public static final String SOURCE_TOPIC_KEY = "topics";
    public static final String TARGET_TOPIC_KEY = "sinkTopic";
    public static final String BOOTSTRAP_SERVERS_KEY = "bootstrapServers";

    public static final String TABLE_NAME_VALUE = "ods.user_events";

    public static List<String> getParameterList(ParameterTool parameterTool, String key){
        return StrUtil.splitTrim(parameterTool.getRequired(key), ',');
    }

    public static String getParameterString(ParameterTool parameterTool, String key){
        return parameterTool.getRequired(key);
    }
}
