package com.example.kafkacommon.dto.weather;

import com.example.kafkacommon.dto.AbstractDto;
import lombok.Data;

@Data
public class ResponseWeatherDto implements AbstractDto {
    private String userId;
    private String message;

    public void append(String msg) {
        if (message == null) {
            message = msg + "\n";
        } else {
            message += msg + "\n";
        }
    }
}
