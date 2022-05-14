package com.example.wservice.kafka;

import com.example.kafkacommon.dto.GetWeatherDto;
import com.example.wservice.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Slf4j
@Component
@KafkaListener(topics = "telegram.service")
public class WeatherConsumer {
    private final WeatherService service;

    public WeatherConsumer(WeatherService service) {
        this.service = service;
    }

    @KafkaHandler
    public void listener(GetWeatherDto message) throws MalformedURLException {
        log.info(String.format("Received message: %s", message));
        switch (message.getForecastType()) {
            case DAY:
                service.getForecastOneDay(message);
                break;
            case FIVE_DAYS:
                service.getForecastFiveDays(message);
                break;
        }
    }
}
