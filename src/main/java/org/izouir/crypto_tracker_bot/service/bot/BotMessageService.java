package org.izouir.crypto_tracker_bot.service.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class BotMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotMessageService.class);

    public void sendAsync(final TelegramLongPollingBot bot, final Long chatId, final String text) {
        try {
            bot.executeAsync(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (final TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendMessageAsync(final TelegramLongPollingBot bot, final SendMessage message) {
        try {
            bot.executeAsync(message);
        } catch (final TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void deleteAsync(final TelegramLongPollingBot bot, final Long chatId, final Integer messageId) {
        try {
            bot.executeAsync(DeleteMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .build());
        } catch (final TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
