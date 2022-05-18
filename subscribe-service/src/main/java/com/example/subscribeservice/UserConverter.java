package com.example.subscribeservice;

import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.subscribeservice.common.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserConverter implements Converter<SubscribeDto, User> {
    @Override
    public User convert(SubscribeDto source) {
        Assert.notNull(source, "Source cannot be null!");

        User user = new User();
        user.setTelegramId(source.getTelegramId());
        user.setUserName(source.getUserName());
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        return user;
    }
}
