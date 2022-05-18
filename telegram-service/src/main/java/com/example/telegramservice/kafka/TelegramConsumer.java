package com.example.telegramservice.kafka;

import com.example.kafkacommon.dto.subscribe.SubscribeCitiesDto;
import com.example.kafkacommon.dto.subscribe.SubscribeDto;
import com.example.kafkacommon.dto.weather.ResponseWeatherDto;
import com.example.telegramservice.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@KafkaListener(topics = {"weather.service", "subscribe.service"})
public class TelegramConsumer {
    private final TelegramBotService bot;

    public TelegramConsumer(TelegramBotService bot) {
        this.bot = bot;
    }

    @KafkaHandler
    public void weatherListener(ResponseWeatherDto message) {
        Assert.notNull(message, "Message must be not null!");

        String userId = message.getUserId();
        String text = message.getMessage();
        Assert.notNull(userId, "UserId must be not null!");
        Assert.notNull(text, "Text must be not null!");

        log.info(String.format("Received message: %s", message));

        trySendMessage(userId, text);
    }

    @KafkaHandler
    public void subscribeListener(SubscribeDto message) {
        Assert.notNull(message, "Message must be not null!");
        String telegramId = message.getTelegramId();
        StringBuilder sb = new StringBuilder();
        switch (message.getEvent()) {
            case SUBSCRIBED: {
                sb.append("Карточка на прогноз в г. ");
                sb.append(message.getCityName());
                sb.append(" прошла успешна, частота уведомлений: ");
                sb.append(message.getPeriod());
                sb.append("ч.");
            }
                break;
            case UNSUBSCRIBED: {
                sb.append("Ты отказался от рассылки погоды по г. ");
                sb.append(message.getCityName());
            }
                break;
            case ERROR: {
                sb.append("Произошла ошибка!");
                sb.append("\n");
                sb.append(message.getErrorMessage());
            }
                break;
        }
        trySendMessage(telegramId, sb.toString());
    }

    @KafkaHandler
    public void subscribeCitiesListener(SubscribeCitiesDto message) {
        Assert.notNull(message, "Message must be not null!");
        String telegramId = message.getTelegramId();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Список городов на которые имеется подписка:</b>\n");
        sb.append(String.join(", ", message.getCities()));
        sb.append("\n");
        sb.append("<i>Для отписки введите команду /unsub НазваниеГорода</i>"); //TODO сделать кнопки для обработки колбэков и отписки в v2.0
        trySendMessage(telegramId, sb.toString());
    }

    private void trySendMessage(String telegramId, String sb) {
        try {
            bot.execute(SendMessage.builder()
                    .chatId(telegramId)
                    .text(sb)
                    .parseMode(ParseMode.HTML)
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error(String.format("Произошла ошибка при отправке сообщения пользователю %s", telegramId));
        }
    }
}
