package com.example.telegramservice;

import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.kafkacommon.dto.subscribe.SubscribeEvent;
import com.example.telegramservice.kafka.TelegramProducer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class SubscribeService {
    private final TelegramProducer producer;

    public SubscribeService(TelegramProducer producer) {
        this.producer = producer;
    }

    public void createUser(User user) {
        SubscribeDto dto = new SubscribeDto();
        dto.setTelegramId(user.getId().toString());
        dto.setUserName(user.getUserName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEvent(SubscribeEvent.CREATE);
        producer.sendMessage(dto);
    }

    public void subscribe(User user, String cityName, int periodInHour) {
        SubscribeDto dto = new SubscribeDto();
        dto.setTelegramId(user.getId().toString());
        dto.setCityName(cityName);
        dto.setPeriod(periodInHour);
        dto.setEvent(SubscribeEvent.SUBSCRIBE);
        producer.sendMessage(dto);
    }

    public void subscribeList(String telegramId) {
        SubscribeDto dto = new SubscribeDto();
        dto.setTelegramId(telegramId);
        dto.setEvent(SubscribeEvent.SUBSCRIBE_LIST);
        producer.sendMessage(dto);
    }

    public void unSubscribe(String telegramId, String cityName) {
        SubscribeDto dto = new SubscribeDto();
        dto.setTelegramId(telegramId);
        dto.setCityName(cityName);
        dto.setEvent(SubscribeEvent.UNSUBSCRIBE);
        producer.sendMessage(dto);
    }
}
