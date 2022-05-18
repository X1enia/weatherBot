package com.example.telegramservice.command;

import com.example.telegramservice.SubscribeService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class UnSubscribeCommand implements ICommand {
    private static final String TEXT_TRIGGER = "/unsub";
    private final SubscribeService service;

    public UnSubscribeCommand(SubscribeService service) {
        this.service = service;
    }

    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public BotApiMethod<Message> execute(Message message) {
        String userId = message.getFrom().getId().toString();
        String[] split = message.getText().split(TEXT_TRIGGER);
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(userId).parseMode(ParseMode.HTML);
        try {
            String cityName = split[1].split(" ")[1];
            service.unSubscribe(userId, cityName);
            return builder.text(String.format("Карточка сильно мятая, но я попробую отписать тебя от прогноза погоды по городу %s", cityName))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return builder.text("Введи название города от которого хочешь отписаться \uD83E\uDD19 \nПример: /unsub Москва") //todo добавить локализацию
                    .build();
        }
    }
}
