package com.example.wservice;

import com.example.wservice.dto.ForecastFiveDaysDto;
import com.example.wservice.dto.WeatherDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class WeatherService {
    private final String apiToken;
    private final String apiUrlOneDay;
    private final String apiUrlFiveDay;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(@Value("${weather.config.token}") String apiToken,
                          @Value("${weather.config.urlOneDay}") String apiUrlOneDay,
                          @Value("${weather.config.urlFiveDay}") String apiUrlFiveDay) {
        this.apiToken = apiToken;
        this.apiUrlOneDay = apiUrlOneDay;
        this.apiUrlFiveDay = apiUrlFiveDay;
    }

    public ForecastResponse getForecastOneDay(String cityName) throws MalformedURLException {
        URL weatherUrl = new URL(String.format(apiUrlOneDay, cityName, apiToken));
        try (InputStream is = (weatherUrl.openStream())) {
            WeatherDto dto = objectMapper.readValue(is, WeatherDto.class); //здесь будет ошибка подумать как это исправить
            return ForecastResponse.builder()
                    .errorCode(0)
                    .body(dto)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return ForecastResponse.builder()
                    .errorCode(1)
                    .body(e.getMessage())
                    .build();
        }
    }

    public ForecastResponse getForecastFiveDays(String cityName) throws MalformedURLException {
        URL weatherUrl = new URL(String.format(apiUrlFiveDay, cityName, apiToken));
        try (InputStream is = (weatherUrl.openStream())) {
            ForecastFiveDaysDto dto = objectMapper.readValue(is, ForecastFiveDaysDto.class); //здесь будет ошибка подумать как это исправить
            return ForecastResponse.builder()
                    .errorCode(0)
                    .body(dto)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return ForecastResponse.builder()
                    .errorCode(1)
                    .body(e.getMessage())
                    .build();
        }
    }
}
