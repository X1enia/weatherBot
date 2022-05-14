package com.example.telegramservice;

import com.example.telegramservice.command.ICommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Configuration
public class TelegramBotConfig {
    @Bean
    public SetWebhook webhook(@Value("${telegram-config.url}") String url) {
        return SetWebhook.builder()
                .url(url)
                .build();
    }

    @Bean
    public TelegramBotService telegramBotService(
            SetWebhook webhook,
            @Value("${telegram-config.name}") String botName,
            @Value("${telegram-config.token}") String botToken,
            @Value("${telegram-config.path}") String botPath,
            List<ICommand> commandList
    ) throws TelegramApiException {
        TelegramBotService botService = new TelegramBotService(webhook, botName, botToken, botPath, commandList);
        botService.setWebhook(webhook);
        return botService;
    }
}
