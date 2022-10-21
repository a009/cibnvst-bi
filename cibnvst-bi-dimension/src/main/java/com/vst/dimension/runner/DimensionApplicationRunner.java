package com.vst.dimension.runner;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.LineIter;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.vst.dimension.repository.RepositoryManager;
import com.vst.dimension.repository.AbstractBaseRepository;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author fucheng
 * @date 2022/10/21
 */
@Component
@Slf4j
public class DimensionApplicationRunner implements ApplicationRunner {

    @Value("${spring.movie-download-url}")
    String movieDownloadUrl;

    @Value("${spring.topic-download-url}")
    String topicDownloadUrl;

    @Value("${spring.submit-batch}")
    Integer submitBatch;

    @Autowired
    RepositoryManager repositoryManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Arrays.asList(topicDownloadUrl, movieDownloadUrl).forEach(this::flushToRedis);
    }

    private void flushToRedis(String url){
        String keyword = ReUtil.getGroup1(".+?filename\\=(.+)\\.dat$", url);

        log.info("url {} keyword {}", url, keyword);

        Class<?> clazz = getClassByType(keyword);

        obtainUrlValueIntoRedis(url, clazz);
    }

    private Class<?> getClassByType(String keyword){
        try {
            return Class.forName("com.vst.dimension.entity." + StrUtil.upperFirst(keyword));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取实际的处理者
     * @param clazz
     * @return
     */
    private AbstractBaseRepository obtainActualHandler(Class<?> clazz){
        AbstractBaseRepository abstractBaseRepository = repositoryManager.getAbstractBaseRepository(clazz);
        return Assert.notNull(abstractBaseRepository);
    }

    /**
     * 解析URL的值并插入到Redis
     * @param url
     * @param clazz
     */
    private void obtainUrlValueIntoRedis(String url, Class<?> clazz) {
        //解析URL的数据
        try{
            @Cleanup InputStream inputStream = HttpRequest.get(url).execute().bodyStream();

            byte[] content = ZipUtil.unGzip(inputStream, 2048);

            InputStream byteArrayInputStream = IoUtil.toStream(content);

            @Cleanup LineIter lineIter = IoUtil.lineIter(byteArrayInputStream, Charset.defaultCharset());

            Iterator<?> beanIter = IterUtil.trans(lineIter, line -> JSONUtil.toBean(line, clazz));

            obtainActualHandler(clazz).saveAll(beanIter, submitBatch);
        }catch (Exception e){
            log.error("obtain value url {} error ", url, e);
        }
    }
}
