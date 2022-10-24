package com.vst.api.config;

import com.vst.api.common.GzipArgumentResolver;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer{
    private GzipArgumentResolver gzipArgumentResolver;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(gzipArgumentResolver);
    }
}
