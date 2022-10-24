package com.vst.api;

import cn.hutool.core.util.ZipUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext
public class ApiControllerTest {

    @Test
    public void test(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/").content(mockData()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    private byte[] mockData(){
        //language=JSON
        String jsonStr = "{\"name\": \"zhangSan\", \"age\": 22, \"birthDay\": \"2022-10-21 23:59:59\"}";
        return ZipUtil.gzip(jsonStr.getBytes(StandardCharsets.UTF_8));
    }

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "test")
    private void consumerTest(ConsumerRecord consumerRecord){
        System.out.println(consumerRecord.value());
    }
}
