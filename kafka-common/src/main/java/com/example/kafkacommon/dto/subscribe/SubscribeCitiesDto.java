package com.example.kafkacommon.dto.subscribe;

import com.example.kafkacommon.dto.AbstractDto;
import lombok.Data;

import java.util.List;

@Data
public class SubscribeCitiesDto implements AbstractDto {
    private String telegramId;
    private List<String> cities;
}
