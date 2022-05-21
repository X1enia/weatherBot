package com.example.telegramservice.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class HelpCommand implements ICommand{
    private static final String TEXT_TRIGGER = "/help";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());
    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public BotApiMethod<Message> execute(Message message) {
        String userId = message.getFrom().getId().toString();
        return SendMessage.builder()
                .chatId(userId)
                .text(String.format(RESOURCE_BUNDLE.getString("app.help")))
                .parseMode(ParseMode.HTML)
                .build();
    }
}
