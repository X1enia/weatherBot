package com.example.kafkacommon.dto.subscribe;

import com.example.kafkacommon.dto.AbstractDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscribeDto implements AbstractDto {
    private String telegramId;
    private String userName;
    private String firstName;
    private String lastName;
    private String cityName;
    private Integer period;
    private SubscribeEvent event;
    private String errorMessage;

    public boolean createComplete() {
        return userName != null || firstName != null || lastName != null;
    }

    public boolean subscribeComplete() {
        return cityName != null && period != null;
    }
}
