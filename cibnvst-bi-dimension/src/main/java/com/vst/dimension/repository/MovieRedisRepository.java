package com.vst.dimension.repository;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.vst.dimension.entity.Movie;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author fucheng
 * @date 2022/10/21
 */
@Component
public class MovieRedisRepository extends AbstractBaseRepository<Movie> {

    public MovieRedisRepository(ValueOperations<String,String> valueOperations) {
        super(valueOperations);
    }

    @Override
    public String getRedisKey(Movie movie) {
        return "movie::" + movie.getUuid();
    }

    @Override
    public String getRedisValue(Movie movie) {
        movie.setLastTime(DateTime.now());
        return JSONUtil.toJsonStr(movie);
    }
}
