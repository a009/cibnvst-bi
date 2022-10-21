package com.vst.api.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vst.api.system.properties.KafkaProducerProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author fucheng
 * @date 2022/10/20
 * 将事件记录到kafka中
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaSendUserEventListener implements ApplicationListener<UserEvent> {

    ObjectMapper objectMapper;

    KafkaTemplate<String, String> kafkaTemplate;

    KafkaProducerProperties kafkaProducerProperties;

    @Override
    public void onApplicationEvent(UserEvent event) {
        String data = convertString(event.getSource());
        if (ObjectUtils.isEmpty(data)){
            return;
        }

        String topic = kafkaProducerProperties.getTopic();
        kafkaTemplate.send(topic, data);
    }

    private String convertString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("convert json error, ", e);
        }
        return null;
    }
}


