package org.izouir.crypto_tracker_bot.service.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BotUpdateService {
    private static final ExecutorService EXECUTOR = Executors.newWorkStealingPool();
    private final BotMessageService botMessageService;

    @Autowired
    public BotUpdateService(final BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    public void executeUpdateAsync(final TelegramLongPollingBot bot, final Update update) {
        EXECUTOR.execute(() -> {
            final Long chatId = update.getMessage().getChatId();
            final String text = update.getMessage().getText();
            botMessageService.sendAsync(bot, chatId, text);
        });
    }
}
