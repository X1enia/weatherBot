package com.example.subscribeservice.kafka;

import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.subscribeservice.subscribe.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@KafkaListener(topics = "telegram.service")
public class SubscribeConsumer {
    private final SubscribeService service;

    public SubscribeConsumer(SubscribeService service) {
        this.service = service;
    }

    @KafkaHandler
    public void listener(SubscribeDto message) {
        Assert.notNull(message, "Message must be not null!");

        switch (message.getEvent()) {
            case CREATE:
                service.createUser(message);
                break;
            case SUBSCRIBE:
                service.subscribeUser(message);
                break;
            case UNSUBSCRIBE:
                service.unsubscribe(message);
                break;
            case SUBSCRIBE_LIST:
                service.sendSubscribeList(message);
                break;
        }
    }
}
