package com.example.kafkacommon.dto;

import lombok.Data;

@Data
public class GetWeatherDto implements AbstractDto {
    private String telegramId;
    private String cityName;
    private ForecastType forecastType;
}
