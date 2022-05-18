package com.example.kafkacommon.dto.weather;

import com.example.kafkacommon.dto.AbstractDto;
import lombok.Data;

@Data
public class GetWeatherDto implements AbstractDto {
    private String telegramId;
    private String cityName;
    private ForecastType forecastType;
}
