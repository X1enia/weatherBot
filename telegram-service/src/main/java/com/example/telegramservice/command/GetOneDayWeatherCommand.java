package com.example.telegramservice.command;

import com.example.kafkacommon.dto.weather.ForecastType;
import com.example.kafkacommon.dto.weather.GetWeatherDto;
import com.example.telegramservice.SubscribeService;
import com.example.telegramservice.kafka.TelegramProducer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class GetOneDayWeatherCommand implements ICommand { //todo сделать inline отображение погоды
    private static final String TEXT_TRIGGER = "/weather";
    private final TelegramProducer producer; //todo как-то убрать продьюсер в сервис мб
    private final SubscribeService service;
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());


    public GetOneDayWeatherCommand(TelegramProducer producer, SubscribeService service) {
        this.producer = producer;
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
            GetWeatherDto dto = new GetWeatherDto();
            dto.setTelegramId(userId.toString());
            dto.setCityName(cityName);
            dto.setForecastType(ForecastType.DAY);
            producer.sendMessage(dto);
            service.createUser(message.getFrom());
            return SendMessage.builder()
                    .chatId(String.valueOf(userId))
                    .text(String.format(RESOURCE_BUNDLE.getString("app.weatherOneDay"), cityName))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return SendMessage.builder()
                    .chatId(String.valueOf(userId))
                    .text(String.format(RESOURCE_BUNDLE.getString("app.textOneDay")))
                    .build();
        }
    }
}
