package com.vst.api.system.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@Configuration
@ConfigurationProperties(prefix = "spring.kafka.producer")
@Data
public class KafkaProducerProperties {
    private String topic;
}
