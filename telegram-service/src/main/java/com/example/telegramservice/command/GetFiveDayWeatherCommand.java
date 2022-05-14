package com.example.telegramservice.command;

import com.example.kafkacommon.dto.ForecastType;
import com.example.kafkacommon.dto.GetWeatherDto;
import com.example.telegramservice.kafka.TelegramProducer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class GetFiveDayWeatherCommand implements ICommand {
    private static final String TEXT_TRIGGER = "/weather5";
    private final TelegramProducer producer;

    public GetFiveDayWeatherCommand(TelegramProducer producer) {
        this.producer = producer;
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
            GetWeatherDto dto = new GetWeatherDto();
            dto.setTelegramId(userId.toString());
            dto.setCityName(cityName);
            dto.setForecastType(ForecastType.FIVE_DAYS);
            producer.sendMessage(dto);
            return builder.text(String.format("Смотрю для тебя погоду в %s", cityName))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return builder.text("Введи название города \uD83E\uDD19") //todo добавить локализацию
                    .build();
        }
    }
}
