package com.example.subscribeservice.subscribe;

import com.example.kafkacommon.dto.subscribe.SubscribeCitiesDto;
import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.kafkacommon.dto.subscribe.SubscribeEvent;
import com.example.subscribeservice.SubscribeConverter;
import com.example.subscribeservice.UserConverter;
import com.example.subscribeservice.common.Subscribe;
import com.example.subscribeservice.common.User;
import com.example.subscribeservice.kafka.SubscribeProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubscribeService {
    private final MongoTemplate mongoTemplate;
    private final SubscribeProducer producer;
    private final UserConverter userConverter;
    private final SubscribeConverter subscribeConverter;

    public SubscribeService(MongoTemplate mongoTemplate,
                            SubscribeProducer producer,
                            UserConverter userConverter, SubscribeConverter subscribeConverter) {
        this.mongoTemplate = mongoTemplate;
        this.producer = producer;
        this.userConverter = userConverter;
        this.subscribeConverter = subscribeConverter;
    }

    public void createUser(SubscribeDto message) {
        Assert.notNull(message.getTelegramId(), "Telegram ID must be not null!");
        SubscribeDto send = new SubscribeDto();
        String telegramId = message.getTelegramId();
        send.setTelegramId(telegramId);
        if (message.createComplete()) {
            User user = userConverter.convert(message);
            if (user == null) {
                log.error(String.format("User must be not null! Telegram id: %s", telegramId));
            } else {
                user.setRegDate(LocalDateTime.now());
                Query query = findSubByTelegramId(telegramId);
                User userExist = mongoTemplate.findOne(query, User.class);
                if (userExist == null) {
                    mongoTemplate.save(user);
                    send.setEvent(SubscribeEvent.CREATED);
                    send.setTelegramId(user.getTelegramId());
                } else {
                    log.info(String.format("User exist. Telegram id: %s", telegramId));
                }
            }
            producer.sendMessage(send);
        } else {
            log.error("Message for creating is not completed! Need username or first/last name. Telegram id: {telegramId}");
        }
    }

    public void subscribeUser(SubscribeDto message) {
        String telegramId = message.getTelegramId();
        Assert.notNull(telegramId, "Telegram ID must be not null!");
        SubscribeDto send = new SubscribeDto();
        send.setTelegramId(telegramId);
        if (message.subscribeComplete()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("telegramId").is(telegramId));
            User user = mongoTemplate.findOne(query, User.class);
            if (user == null) {
                log.error(String.format("User with telegram id: %s not found!", telegramId));
            } else {
                String cityName = message.getCityName();
                if (subscribeExist(telegramId, cityName)) {
                    send.setEvent(SubscribeEvent.ERROR);
                    send.setErrorMessage(String.format("Ты уже подписался на погоду в городе %s", cityName));
                } else {
                    Subscribe subscribe = subscribeConverter.convert(message);
                    mongoTemplate.save(subscribe);
                    send.setEvent(SubscribeEvent.SUBSCRIBED);
                    send.setTelegramId(user.getTelegramId());
                    send.setCityName(cityName);
                    send.setPeriod(message.getPeriod());
                }
            }
            producer.sendMessage(send);
        } else {
            log.error(String.format("Message for creating is not completed! Need city name or period. Telegram ID: %s", telegramId));
        }
    }

    private boolean subscribeExist(String telegramId, String cityName) {
        Query query = findSubByTelegramIdAndCityName(telegramId, cityName);
        query.addCriteria(Criteria.where("isActive").is(true));
        Subscribe one = mongoTemplate.findOne(query, Subscribe.class);
        return one != null;
    }

    public void unsubscribe(SubscribeDto message) {
        String telegramId = message.getTelegramId();
        String cityName = message.getCityName();
        Assert.notNull(telegramId, "Telegram ID must be not null!");
        Assert.notNull(cityName, "City name must be not null!");

        SubscribeDto send = new SubscribeDto();
        send.setTelegramId(telegramId);
        send.setCityName(cityName);
        Query query = findSubByTelegramIdAndCityName(telegramId, cityName);

        Subscribe subscribe = mongoTemplate.findOne(query, Subscribe.class);
        if (subscribe == null) {
            log.error(String.format("Subscribe for user telegram id: %s and city %s not found!", telegramId, cityName));
            send.setEvent(SubscribeEvent.ERROR);
            send.setErrorMessage(String.format("Ты не подписывался на прогноз в городе %s", cityName));
        } else {
            subscribe.setIsActive(false);
            mongoTemplate.save(subscribe);
            send.setEvent(SubscribeEvent.UNSUBSCRIBED);
        }
        producer.sendMessage(send);
    }

    public void sendSubscribeList(SubscribeDto message) {
        String telegramId = message.getTelegramId();
        Assert.notNull(telegramId, "Telegram ID must be not null!");

        SubscribeCitiesDto send = new SubscribeCitiesDto();
        Query query = findSubByTelegramId(telegramId);
        List<Subscribe> subscribes = mongoTemplate.find(query, Subscribe.class);
        List<String> cities = subscribes.stream()
                .map(Subscribe::getCityName)
                .collect(Collectors.toList());
        send.setCities(cities);
        send.setTelegramId(telegramId);

        producer.sendMessage(send);
    }

    private Query findSubByTelegramIdAndCityName(String telegramId, String cityName) {
        Query query = findSubByTelegramId(telegramId);
        query.addCriteria(Criteria.where("cityName").is(cityName));
        return query;
    }

    private Query findSubByTelegramId(String telegramId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("telegramId").is(telegramId));
        return query;
    }
}
