package com.example.telegramservice.command;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Интерфейс команды инкапсулирует механизм отправки различных видов сообщений в TELEGRAM BOT API
 */
public interface ICommand {
    boolean canExecute(String text);
    BotApiMethod<Message> execute(Message message);
}
