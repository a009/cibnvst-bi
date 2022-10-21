package com.vst.dimension.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/21
 */
@AllArgsConstructor
public abstract class AbstractBaseRepository<T> {
    protected final ValueOperations<String, String> valueOperations;

    public void saveAll(Iterator<T> iterator, int batchSize){
        Map<String, String> batchMap = new HashMap<>();
        int index = 0;

        while (iterator.hasNext()) {
            T t = iterator.next();

            String redisKey = getRedisKey(t);
            String redisValue = getRedisValue(t);

            index++;
            batchMap.put(redisKey, redisValue);

            if (index % batchSize == 0){
                valueOperations.multiSet(batchMap);
                batchMap.clear();
            }
        }

        if (!batchMap.isEmpty()) {
            valueOperations.multiSet(batchMap);
        }
    }

    public abstract String getRedisKey(T t);

    public abstract String getRedisValue(T t);
}
