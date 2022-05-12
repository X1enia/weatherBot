package com.example.kafkacommon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto implements AbstractDto {
    private String name;
    private List<WeaDto> weather;
    private String dt_txt;
    private SysDto sys;
    private Long dt;

    @JsonProperty
    private MainDto main;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainDto {
        private double temp;
        private double feels_like;
        private int pressure;
        private int humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeaDto {
        private String main;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysDto {
        private String country;
        private Long sunrise;
        private Long sunset;
    }
}
