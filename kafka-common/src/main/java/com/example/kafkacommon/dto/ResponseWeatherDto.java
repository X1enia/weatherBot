package com.example.kafkacommon.dto;

import lombok.Data;

@Data
public class ResponseWeatherDto implements AbstractDto {
    private String message;

    public void append(String msg) {
        if (message == null) {
            message = msg;
        } else {
            message += msg;
        }
    }
}
