package com.example.subscribeservice;

import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.subscribeservice.common.Subscribe;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Component
public class SubscribeConverter implements Converter<SubscribeDto, Subscribe> {
    @Override
    public Subscribe convert(SubscribeDto source) {
        Assert.notNull(source, "Source cannot be null!");

        Subscribe subscribe = new Subscribe();
        subscribe.setTelegramId(source.getTelegramId());
        subscribe.setCityName(source.getCityName());
        subscribe.setPeriod(source.getPeriod());
        subscribe.setLastTimeSend(getLocalTimeZero());
        subscribe.setIsActive(true);
        return subscribe;
    }

    private LocalDateTime getLocalTimeZero() {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                now.getHour(),
                0,
                0);
    }
}
