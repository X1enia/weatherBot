package com.example.telegramservice.command;

import com.example.telegramservice.SubscribeService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SubscribeListCommand implements ICommand {
    private static final String TEXT_TRIGGER = "/my_subs";
    private final SubscribeService service;

    public SubscribeListCommand(SubscribeService service) {
        this.service = service;
    }

    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public BotApiMethod<Message> execute(Message message) {
        String userId = message.getFrom().getId().toString();
        service.subscribeList(userId);
        return SendMessage.builder()
                .chatId(userId)
                .text("Пошел смотреть коллекцию твоих подписок, один момент!")
                .build();
    }
}
