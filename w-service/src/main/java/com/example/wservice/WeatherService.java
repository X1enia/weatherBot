package com.example.wservice;

import com.example.kafkacommon.dto.weather.ForecastFiveDaysDto;
import com.example.kafkacommon.dto.weather.GetWeatherDto;
import com.example.kafkacommon.dto.weather.ResponseWeatherDto;
import com.example.kafkacommon.dto.weather.WeatherDto;
import com.example.wservice.kafka.WeatherProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class WeatherService {
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());
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

            int currentDay = LocalDate.now().getDayOfMonth();
            boolean first = false;
            for (WeatherDto weatherDto : dto.getList()) {
                if (!first) {
                    response.append(convertWeatherToString(weatherDto, dto.getCity().getName()));
                    first = true;
                } else {
                    LocalDateTime date = Instant.ofEpochMilli(weatherDto.getDt() * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if (date.getDayOfMonth() > currentDay) {
                        response.append(convertWeatherToString(weatherDto, dto.getCity().getName()));
                        currentDay += 1;
                    }
                }
            }
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
            textDate = RESOURCE_BUNDLE.getString("app.weather.day");
        } else {
            LocalDateTime date = convertMsToDate(dto.getDt());
            String monthName = RESOURCE_BUNDLE.getString(date.getMonth().toString());
            textDate = String.format(RESOURCE_BUNDLE.getString("app.weather.format"), date.getDayOfMonth(), monthName, date.getYear()); //todo перевод названий месяцев
        }
        String result = String.format(RESOURCE_BUNDLE.getString("app.weather.5days"), textDate, cityName); //TODO докрутить красивое описание погоды
        result += "\n";
        result += String.format(RESOURCE_BUNDLE.getString("app.weather.getMain.format"),
                getWeatherDescription(dto.getWeather().get(0).getDescription()),
                dto.getMain().getTemp(),
                dto.getMain().getFeels_like(),
                dto.getMain().getPressure(),
                dto.getMain().getHumidity());
        result += "\n";
        result += getSunSetRise(dto.getSys());
        result += "\n";
        return result;
    }

    private LocalDateTime convertMsToDate(Long ms) {
        return Instant.ofEpochMilli(ms * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String getSunSetRise(WeatherDto.SysDto sys) {
        Long sunrise = sys.getSunrise();
        Long sunset = sys.getSunset();
        if (sunrise != null && sunset != null) {
            return String.format(RESOURCE_BUNDLE.getString("app.weather.sys.format"), convertMsToDate(sunrise), convertMsToDate(sunset));
        }
        return "";
    }

    private String getWeatherDescription(String description) {
        return description.substring(0,1).toUpperCase(Locale.ROOT) + description.substring(1);
    }
}
