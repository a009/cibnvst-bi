package com.vst.api.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vst.api.entity.RequestInfo;
import com.vst.api.service.IExternalSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/24
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaProducerSenderImpl implements IExternalSender {
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    @Override
    public void send(JsonNode jsonNode) {
        doSend(jsonNode);
    }

    private void doSend(JsonNode jsonNode){
        HttpServletRequest request = currentRequestAttributes().getRequest();

        Collection<Map> records = getMaps(jsonNode);

        records.forEach(record -> {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setClientIP(ServletUtil.getClientIP(request));
            requestInfo.setContentType(request.getContentType());
            requestInfo.setRequestURI(request.getRequestURI());
            requestInfo.setRequestURL(request.getRequestURL().toString());
            requestInfo.setServerPort(request.getServerPort());
            requestInfo.setServerName(request.getServerName());
            requestInfo.setMethod(request.getMethod());
            requestInfo.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            requestInfo.setRequestTime(System.currentTimeMillis());
            requestInfo.setRecord(record);

            try {
                String message = objectMapper.writeValueAsString(requestInfo);
                kafkaTemplate.sendDefault(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Collection<Map> getMaps(JsonNode jsonNode) {
        Collection<Map> records =
                jsonNode.isArray() ?
                        objectMapper.convertValue(jsonNode, Collection.class) :
                        Collections.singletonList(objectMapper.convertValue(jsonNode, Map.class));
        return records;
    }

    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes) requestAttr;
    }
}
