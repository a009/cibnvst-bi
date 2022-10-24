package com.vst.api.common;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ZipUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fucheng
 * @date 2022/10/23
 */
@Component
public class GzipArgumentResolver implements HandlerMethodArgumentResolver {
    private ObjectMapper objectMapper;

    public GzipArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UnZipToJsonNode.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = RequestTools.currentRequestAttributes().getRequest();

        checkInputStream(request);

        JsonNode jsonNode = parseInputStreamAsJson(request);

        checkJsonType(jsonNode);

        return jsonNode;
    }

    private JsonNode parseInputStreamAsJson(HttpServletRequest request) throws Exception {
        byte[] content = ZipUtil.unGzip(request.getInputStream());
        return objectMapper.readTree(content);
    }

    private void checkInputStream(HttpServletRequest request) {
        Assert.isFalse(request.getContentLength() == 0, "Empty data flow");
    }

    private void checkJsonType(JsonNode jsonNode) {
        Assert.isTrue(jsonNode.isArray() || jsonNode.isObject(), "Invalid json format " + jsonNode);
    }
}
