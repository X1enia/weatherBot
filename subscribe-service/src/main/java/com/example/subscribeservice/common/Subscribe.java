package com.example.subscribeservice.common;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("subscribes")
public class Subscribe {
    @Id
    private String id;
    private String telegramId;
    private String cityName;
    private Integer period;
    private LocalDateTime lastTimeSend;
    private Boolean isActive;
}
