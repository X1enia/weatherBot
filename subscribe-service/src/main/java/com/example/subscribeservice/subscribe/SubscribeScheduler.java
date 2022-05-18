package com.example.subscribeservice.subscribe;

import com.example.kafkacommon.dto.weather.ForecastType;
import com.example.kafkacommon.dto.weather.GetWeatherDto;
import com.example.subscribeservice.common.Subscribe;
import com.example.subscribeservice.kafka.SubscribeProducer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SubscribeScheduler {
    private final SubscribeProducer producer;
    private final MongoTemplate mongoTemplate;

    public SubscribeScheduler(SubscribeProducer producer, MongoTemplate mongoTemplate) {
        this.producer = producer;
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(fixedRate = 5000)
    private void sender() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isActive").is(true));
        List<Subscribe> subscribes = mongoTemplate.find(query, Subscribe.class);
        if (CollectionUtils.isEmpty(subscribes)) {
            return;
        }
        List<Subscribe> subscribeReadyToConvert = subscribes.stream()
                .filter(canSendForecast())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(subscribeReadyToConvert)) {
            return;
        }
        updateSubscriptions(subscribeReadyToConvert);
        List<GetWeatherDto> dtoReadyToSend = subscribeReadyToConvert.stream()
                .map(subscribe -> {
                    GetWeatherDto dto = new GetWeatherDto();
                    dto.setCityName(subscribe.getCityName());
                    dto.setTelegramId(subscribe.getTelegramId());
                    dto.setForecastType(ForecastType.DAY);
                    return dto;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dtoReadyToSend)) {
            return;
        }

        dtoReadyToSend.forEach(producer::sendMessage); //TODO в версии 2.0 сделать общую DTO для отправки всех запросов по всем пользователям
        //TODO кэширование прогноза погоды в версии 3.0 во избежание ограничений бесплатного API opeanweather
    }

    private void updateSubscriptions(List<Subscribe> subscribeReadyToConvert) {
        subscribeReadyToConvert
                .forEach(subscribe -> {
                    subscribe.setLastTimeSend(LocalDateTime.now());
                    mongoTemplate.save(subscribe);
                });
    }

    Predicate<Subscribe> canSendForecast() {
        return subscribe -> {
            LocalDateTime nextTime = subscribe.getLastTimeSend().plusHours(subscribe.getPeriod());
            return LocalDateTime.now().isAfter(nextTime);
        };
    }
}
