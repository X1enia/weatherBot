package com.example.wservice;

import com.example.kafkacommon.dto.GetWeatherDto;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Component
@KafkaListener(topics = "weather.service")
public class WeatherConsumer {
    private final WeatherService service;

    public WeatherConsumer(WeatherService service) {
        this.service = service;
    }

    @KafkaHandler
    public void listener(GetWeatherDto message) throws MalformedURLException {
        System.out.printf("Recieved message: %s%n", message);
        switch (message.getForecastType()) {
            case DAY:
                service.getForecastOneDay(message.getCityName());
                break;
            case FIVE_DAYS:
                service.getForecastFiveDays(message.getCityName());
                break;
        }
    }
}
