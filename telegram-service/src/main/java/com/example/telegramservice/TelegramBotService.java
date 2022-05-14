package com.example.telegramservice;

import com.example.telegramservice.command.ICommand;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.List;

@Slf4j
public class TelegramBotService extends SpringWebhookBot {
    private final String botName;
    private final String botToken;
    private final String botPath;
    private final List<ICommand> commandList;

    public TelegramBotService(SetWebhook setWebhook,
                              String botName,
                              String botToken,
                              String botPath,
                              List<ICommand> commandList
    ) {
        super(setWebhook);
        this.botToken = botToken;
        this.botName = botName;
        this.commandList = commandList;
        this.botPath = botPath;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            log.info("Пришло сообщение: {}. Пользователь: {}", message.getText(), message.getFrom());
            commandList.stream()
                    .filter(command -> command.canExecute(message.getText()))
                    .findFirst()
                    .ifPresent(cmd -> {
                        try {
                            execute(cmd.execute(message));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return null;
    }
}
