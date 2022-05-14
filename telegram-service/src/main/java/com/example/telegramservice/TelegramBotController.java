package com.example.telegramservice;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class TelegramBotController {

    private final TelegramBotService bot;

    public TelegramBotController(TelegramBotService bot) {
        this.bot = bot;
    }

    /**
     * Тестовый контроллер для проверки жив бот или нет
     */
    @GetMapping(value = "/test")
    public String testController() {
        return "TelegramBotController работает!";
    }

    @PostMapping(value = "/callback/pathWeather")
    public BotApiMethod<?> getUpdate(@RequestBody Update update) {
        Assert.notNull(update, "Update must be not null!");

        return bot.onWebhookUpdateReceived(update);
    }
}
