package com.vst.api.system.annotation;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author fucheng
 * @date 2022/10/20
 */
public class UnZipJsonArrayResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UnZipJsonArray.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        //Body没数据
        if (httpServletRequest.getContentLength() == 0) {
            throw new IllegalArgumentException("data is empty");
        }

        try {
            byte[] b = ZipUtil.unGzip(httpServletRequest.getInputStream(), 2048);

            String bodyStr = StrUtil.str(b, StandardCharsets.UTF_8);

            if (!JSONUtil.isTypeJSON(bodyStr)) {
                throw new JSONException("Invalid json: " + bodyStr);
            }

            if (JSONUtil.isTypeJSONObject(bodyStr)) {
                bodyStr = StrUtil.wrap(bodyStr, "[", "]");
            }

            return JSONUtil.parseArray(bodyStr).toList(JSONObject.class);
        } catch (IORuntimeException e) {
            throw new IllegalArgumentException("Hutool IO exception: " + e.getMessage());
        } catch (JSONException e) {
            throw new IllegalArgumentException("Json exception: " + e.getMessage());
        } catch (IOException e) {
            throw new IllegalArgumentException("IO exception: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("global exception: " + e.getMessage());
        }
    }
}
