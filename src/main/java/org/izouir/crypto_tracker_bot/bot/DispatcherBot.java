package org.izouir.crypto_tracker_bot.bot;

import org.izouir.crypto_tracker_bot.service.bot.BotUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DispatcherBot extends TelegramLongPollingBot {
    @Value("${TELEGRAM_BOT_USERNAME}")
    private String botUsername;
    private final BotUpdateService botUpdateService;

    @Autowired
    public DispatcherBot(final @Value("${TELEGRAM_BOT_TOKEN}") String botToken,
                         final BotUpdateService botUpdateService) {
        super(botToken);
        this.botUpdateService = botUpdateService;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        botUpdateService.executeUpdateAsync(this, update);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
