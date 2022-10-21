package com.vst.transforms.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import com.vst.transforms.handler.impl.AddressResolverHandler;
import com.vst.transforms.handler.impl.MovieResolverHandler;
import com.vst.transforms.handler.impl.TopicResolverHandler;
import com.vst.transforms.searcher.ISearch;
import com.vst.transforms.searcher.impl.IPSearcher;
import com.vst.transforms.searcher.impl.MovieSearcher;
import com.vst.transforms.searcher.impl.TopicSearcher;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author fucheng
 * @date 2022/10/1
 */
public interface IResolverHandler extends AutoCloseable {
    default void resolver(Map<String, Object> map) {
        String key = MapUtil.getStr(map, getMapKey());
        if (Objects.nonNull(key)){
            map.putAll(getSearch().search(key));
        }
    }
    String getMapKey();

    ISearch getSearch();


    default void close() throws Exception {
        getSearch().close();
    }


    static IResolverHandler createAddressResolverHandler(String ipPath) throws IOException {
        return new AddressResolverHandler(new IPSearcher(ipPath));
    }

    static IResolverHandler createMovieResolverHandler(RedisDS redisDS) throws IOException {
        return new MovieResolverHandler(new MovieSearcher(redisDS));
    }

    static IResolverHandler createTopicResolverHandler(RedisDS redisDS) throws IOException {
        return new TopicResolverHandler(new TopicSearcher(redisDS));
    }
}
