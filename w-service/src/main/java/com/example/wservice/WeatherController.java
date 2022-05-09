package com.example.wservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping(value = "/weather={cityName}")
    public ResponseEntity<ForecastResponse> getForecastByCityName(@PathVariable String cityName) throws MalformedURLException {
        return new ResponseEntity<>(service.getForecastOneDay(cityName), HttpStatus.OK);
    }

    @GetMapping(value = "/weather5={cityName}")
    public ResponseEntity<ForecastResponse> getForecastFiveDaysByCityName(@PathVariable String cityName) throws MalformedURLException {
        return new ResponseEntity<>(service.getForecastFiveDays(cityName), HttpStatus.OK);
    }
}
