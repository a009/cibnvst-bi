package com.vst.dimension.repository;

import com.vst.dimension.entity.Movie;
import com.vst.dimension.entity.Topic;
import com.vst.dimension.repository.AbstractBaseRepository;
import com.vst.dimension.repository.MovieRedisRepository;
import com.vst.dimension.repository.TopicRedisRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/21
 */
@Component
public class RepositoryManager {
    private Map<Class<?>, AbstractBaseRepository> REPOSITORY_MANAGER = new HashMap<>();
    public RepositoryManager(MovieRedisRepository movieRedisRepository, TopicRedisRepository topicRedisRepository) {
        REPOSITORY_MANAGER.put(Movie.class, movieRedisRepository);
        REPOSITORY_MANAGER.put(Topic.class, topicRedisRepository);
    }

    public AbstractBaseRepository getAbstractBaseRepository(Class<?> clazz){
        return REPOSITORY_MANAGER.get(clazz);
    }
}
