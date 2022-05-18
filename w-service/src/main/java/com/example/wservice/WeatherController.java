package com.example.wservice;

import com.example.kafkacommon.dto.weather.GetWeatherDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping(value = "/weather")
    public ResponseEntity<ForecastResponse> getForecastByCityName(@RequestBody GetWeatherDto dto) throws MalformedURLException {
        return new ResponseEntity<>(service.getForecastOneDay(dto), HttpStatus.OK);
    }

    @GetMapping(value = "/weather5")
    public ResponseEntity<ForecastResponse> getForecastFiveDaysByCityName(@RequestBody GetWeatherDto dto) throws MalformedURLException {
        return new ResponseEntity<>(service.getForecastFiveDays(dto), HttpStatus.OK);
    }
}
