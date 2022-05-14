package com.example.telegramservice.kafka;

import com.example.kafkacommon.dto.ResponseWeatherDto;
import com.example.telegramservice.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@KafkaListener(topics = "weather.service")
public class TelegramConsumer {
    private final TelegramBotService bot;

    public TelegramConsumer(TelegramBotService bot) {
        this.bot = bot;
    }

    @KafkaHandler
    public void listener(ResponseWeatherDto message) {
        Assert.notNull(message, "Message must be not null!");

        String userId = message.getUserId();
        String text = message.getMessage();
        Assert.notNull(userId, "UserId must be not null!");
        Assert.notNull(text, "Text must be not null!");

        log.info(String.format("Received message: %s", message));

        try {
            bot.execute(SendMessage.builder()
                    .chatId(userId)
                    .text(text)
                    .parseMode(ParseMode.HTML)
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error(String.format("Произошла ошибка при отправке сообщения пользователю %s", userId));
        }
    }
}
