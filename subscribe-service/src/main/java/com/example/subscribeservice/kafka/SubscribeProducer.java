package com.example.subscribeservice.kafka;

import com.example.kafkacommon.dto.AbstractDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubscribeProducer {
    private final String subscribeTopic;
    private final KafkaTemplate<String, AbstractDto> kafkaTemplate;

    public SubscribeProducer(@Value("${spring.kafka.topic.subscribe}") String subscribeTopic,
                             KafkaTemplate<String, AbstractDto> kafkaTemplate) {
        this.subscribeTopic = subscribeTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(AbstractDto dto) {
        kafkaTemplate.send(new ProducerRecord<>(subscribeTopic, UUID.randomUUID().toString(), dto));
    }
}
