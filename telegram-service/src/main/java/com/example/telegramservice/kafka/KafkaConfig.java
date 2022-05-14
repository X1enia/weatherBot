package com.example.telegramservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic telegramTopic(@Value("${spring.kafka.topic.telegram}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }
}
