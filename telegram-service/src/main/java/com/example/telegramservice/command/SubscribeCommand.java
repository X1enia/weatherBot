package com.example.telegramservice.command;

import com.example.telegramservice.SubscribeService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SubscribeCommand implements ICommand { //todo сделать inline подписку
    private static final String TEXT_TRIGGER = "/subscribe";
    private final SubscribeService service;

    public SubscribeCommand(SubscribeService service) {
        this.service = service;
    }

    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public BotApiMethod<Message> execute(Message message) {
        Long userId = message.getFrom().getId();
        String[] split = message.getText().split(TEXT_TRIGGER);
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(userId.toString()).parseMode(ParseMode.HTML);
        try {
            String cityName = split[1].split(" ")[1];
            int period = Integer.parseInt(split[1].split(" ")[2]);
            if (period < 3 || period > 24) {
                return builder.text("Укажи период от 3 до 24 (значение должно быть целым)")
                        .build();
            }
            service.subscribe(message.getFrom(), cityName, period);
            return builder.text(String.format("Я сейчас узнаю есть ли карточки на подписку по городу %s каждые %s часа(ов)", cityName, period))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return builder.text("Введи название города и период (от 3 и до 24 часов) с каким нужно отправлять сообщения \uD83E\uDD19 \nПример: /subscribe Тверь 5") //todo добавить локализацию
                    .build();
        }
    }
}
