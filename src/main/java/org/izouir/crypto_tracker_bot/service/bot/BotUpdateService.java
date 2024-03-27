package org.izouir.crypto_tracker_bot.service.bot;

import org.izouir.crypto_tracker_bot.service.AuthorizationService;
import org.izouir.crypto_tracker_bot.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.izouir.crypto_tracker_bot.util.constant.service.bot.BotUpdateServiceConstant.*;

@Service
public class BotUpdateService {
    private static final ExecutorService EXECUTOR = Executors.newWorkStealingPool();
    private final BotMessageService botMessageService;
    private final AuthorizationService authorizationService;
    private final CryptoService cryptoService;

    @Autowired
    public BotUpdateService(final BotMessageService botMessageService,
                            final AuthorizationService authorizationService,
                            final CryptoService cryptoService) {
        this.botMessageService = botMessageService;
        this.authorizationService = authorizationService;
        this.cryptoService = cryptoService;
    }

    public void executeUpdateAsync(final TelegramLongPollingBot bot, final Update update) {
        EXECUTOR.execute(() -> {
            if (update.hasMessage() && update.getMessage().hasText()) {
                final Message message = update.getMessage();
                final Long chatId = message.getChatId();
                final String text = message.getText();

                switch (text) {
                    case COMMAND_START -> {
                        final String username = message.getChat().getUserName();
                        authorizationService.authorizeUser(bot, chatId, username);
                    }
                    case COMMAND_TOP -> cryptoService.sendTopCryptoCurrencies(bot, chatId);
                    default -> botMessageService.sendAsync(bot, chatId, COMMAND_UNKNOWN_MESSAGE);
                }
            }
        });
    }
}
