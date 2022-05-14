package com.example.wservice.kafka;


import com.example.kafkacommon.dto.AbstractDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WeatherProducer {

    private final String telegramTopic;
    private final KafkaTemplate<String, AbstractDto> kafkaTemplate;

    public WeatherProducer(KafkaTemplate<String, AbstractDto> kafkaTemplate,
                           @Value("${spring.kafka.topic.weather}") String telegramTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.telegramTopic = telegramTopic;
    }

    public void sendMessage(AbstractDto dto) {
        kafkaTemplate.send(new ProducerRecord<>(telegramTopic, UUID.randomUUID().toString(), dto));
    }
}
