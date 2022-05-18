package com.example.kafkacommon.dto.weather;

import com.example.kafkacommon.dto.AbstractDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastFiveDaysDto implements AbstractDto {
    private List<WeatherDto> list;
    private City city;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
    }
}
