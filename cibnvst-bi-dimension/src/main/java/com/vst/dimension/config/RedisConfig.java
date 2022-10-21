package com.vst.dimension.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author fucheng
 * @date 2022/10/2
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);

        redisTemplate.setEnableDefaultSerializer(false);
        return redisTemplate;
    }

    @Bean
    public ValueOperations<String,String> valueOperations(RedisTemplate<String,String> redisTemplate){
        return redisTemplate.opsForValue();
    }
}
