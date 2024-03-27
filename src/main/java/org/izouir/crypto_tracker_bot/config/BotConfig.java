package org.izouir.crypto_tracker_bot.config;

import org.izouir.crypto_tracker_bot.bot.DispatcherBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfig.class);
    private final DispatcherBot dispatcherBot;

    @Autowired
    public BotConfig(final DispatcherBot dispatcherBot) {
        this.dispatcherBot = dispatcherBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(dispatcherBot);
        } catch (final TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
