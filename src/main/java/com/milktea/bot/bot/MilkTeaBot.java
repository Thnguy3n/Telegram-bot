package com.milktea.bot.bot;

import com.milktea.bot.bot.handler.BotUpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class MilkTeaBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final BotUpdateHandler updateHandler;

    public MilkTeaBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            BotUpdateHandler updateHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Object response = updateHandler.handleUpdate(update);

            if (response instanceof SendMessage) {
                execute((SendMessage) response);
            } else if (response instanceof EditMessageText) {
                execute((EditMessageText) response);
            }
        } catch (TelegramApiException e) {
            log.error("Error sending response to user", e);
        } catch (Exception e) {
            log.error("Error processing update", e);
        }
    }
}
