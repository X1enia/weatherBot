package com.example.telegramservice.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpCommand implements ICommand{
    private static final String TEXT_TRIGGER = "/help";
    @Override
    public boolean canExecute(String text) {
        return text.contains(TEXT_TRIGGER);
    }

    @Override
    public BotApiMethod<Message> execute(Message message) {
        String userId = message.getFrom().getId().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Привет, я бот помогающий с прогнозом погоды, вот список моих команд:</b>\n");
        sb.append("/weather ИмяГорода - подскажет какая погода в городе прямо сейчас!\n");
        sb.append("/weather5 ИмяГорода - тоже самое, но на 5 дней!\n");
        sb.append("<b>Опция подписки за карточки!</b> <i>на самом деле ничего мне не надо кроме верной команды \uD83C\uDF1A</i>\n");
        sb.append("/subscribe ИмяГорода [Время от 3 до 24] - подпишет на постоянной основе на рассылку сообщений по прогнозу погоды раз N часов, здорово, правда?\n");
        sb.append("/unsub ИмяГорода - отказаться от подписки на прогноз погоды, чтобы не доставал больше тебя\n");
        sb.append("/my_subs - покажет куда ты занёс карточки для подписки\n");
        return SendMessage.builder()
                .chatId(userId)
                .text(sb.toString())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
