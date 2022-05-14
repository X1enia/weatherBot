package com.example.wservice;

import com.example.kafkacommon.dto.ForecastFiveDaysDto;
import com.example.kafkacommon.dto.GetWeatherDto;
import com.example.kafkacommon.dto.ResponseWeatherDto;
import com.example.kafkacommon.dto.WeatherDto;
import com.example.wservice.kafka.WeatherProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class WeatherService {
    private final String apiToken;
    private final String apiUrlOneDay;
    private final String apiUrlFiveDay;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WeatherProducer producer;

    public WeatherService(@Value("${weather.config.token}") String apiToken,
                          @Value("${weather.config.urlOneDay}") String apiUrlOneDay,
                          @Value("${weather.config.urlFiveDay}") String apiUrlFiveDay,
                          WeatherProducer producer) {
        this.apiToken = apiToken;
        this.apiUrlOneDay = apiUrlOneDay;
        this.apiUrlFiveDay = apiUrlFiveDay;
        this.producer = producer;
    }

    public ForecastResponse getForecastOneDay(GetWeatherDto message) throws MalformedURLException {
        String cityName = message.getCityName();
        URL weatherUrl = new URL(String.format(apiUrlOneDay, cityName, apiToken));
        try (InputStream is = (weatherUrl.openStream())) {
            WeatherDto dto = objectMapper.readValue(is, WeatherDto.class);
            ResponseWeatherDto response = new ResponseWeatherDto();
            response.append(convertWeatherToString(dto, dto.getName()));
            response.setUserId(message.getTelegramId());
            producer.sendMessage(response);
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

    public ForecastResponse getForecastFiveDays(GetWeatherDto message) throws MalformedURLException {
        String cityName = message.getCityName();
        URL weatherUrl = new URL(String.format(apiUrlFiveDay, cityName, apiToken));
        try (InputStream is = (weatherUrl.openStream())) {
            ForecastFiveDaysDto dto = objectMapper.readValue(is, ForecastFiveDaysDto.class);
            ResponseWeatherDto response = new ResponseWeatherDto();
            dto.getList().forEach(weatherDto -> response.append(convertWeatherToString(weatherDto, dto.getCity().getName())));
            response.setUserId(message.getTelegramId());
            producer.sendMessage(response);
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

    private String convertWeatherToString(WeatherDto dto, String cityName) {
        String textDate;
        if (dto.getDt_txt() == null) {
            textDate = "сегодня";
        } else {
            LocalDateTime date = Instant.ofEpochMilli(dto.getDt() * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
            textDate = String.format("%s %s %s", date.getDayOfMonth(), date.getMonth().toString(), date.getYear()); //todo перевод названий месяцев
        }
        return String.format("Прогноз погоды на: %s в городе: %s\n", textDate, cityName); //TODO докрутить красивое описание погоды
    }
}
