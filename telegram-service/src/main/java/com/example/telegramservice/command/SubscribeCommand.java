package com.example.telegramservice.command;

import com.example.telegramservice.SubscribeService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class SubscribeCommand implements ICommand { //todo сделать inline подписку
    private static final String TEXT_TRIGGER = "/subscribe";
    private final SubscribeService service;
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());

    public SubscribeCommand(SubscribeService service) {
        this.service = service;
    }

    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public SendMessage execute(Message message) {
        Long userId = message.getFrom().getId();
        String[] split = message.getText().split(TEXT_TRIGGER);
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(userId.toString()).parseMode(ParseMode.HTML);
        try {
            String cityName = split[1].split(" ")[1];
            int period = Integer.parseInt(split[1].split(" ")[2]);
            if (period < 3 || period > 24) {
                return SendMessage.builder()
                        .chatId(String.valueOf(userId))
                        .text(String.format(RESOURCE_BUNDLE.getString("app.subTime")))
                        .build();
            }
            service.subscribe(message.getFrom(), cityName, period);
            return SendMessage.builder()
                    .chatId(String.valueOf(userId))
                    .text(String.format(RESOURCE_BUNDLE.getString("app.subCity"), cityName))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return SendMessage.builder()
                    .chatId(String.valueOf(userId))
                    .text(String.format(RESOURCE_BUNDLE.getString("app.sub")))
                    .build();
        }
    }
}
