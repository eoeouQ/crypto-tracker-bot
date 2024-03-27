package org.izouir.crypto_tracker_bot.service.bot;

import org.izouir.crypto_tracker_bot.service.AuthorizationService;
import org.izouir.crypto_tracker_bot.service.CryptoService;
import org.izouir.crypto_tracker_bot.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
    private final SubscriptionService subscriptionService;

    @Autowired
    public BotUpdateService(final BotMessageService botMessageService,
                            final AuthorizationService authorizationService,
                            final CryptoService cryptoService,
                            final SubscriptionService subscriptionService) {
        this.botMessageService = botMessageService;
        this.authorizationService = authorizationService;
        this.cryptoService = cryptoService;
        this.subscriptionService = subscriptionService;
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
                    case COMMAND_SUBSCRIBE -> subscriptionService.subscribe(bot, chatId);
                    case COMMAND_UNSUBSCRIBE -> subscriptionService.unsubscribe(bot, chatId);
                    default -> botMessageService.sendAsync(bot, chatId, COMMAND_UNKNOWN_MESSAGE);
                }
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final Long chatId = callbackQuery.getMessage().getChatId();
                final Integer messageId = callbackQuery.getMessage().getMessageId();
                final String callbackData = callbackQuery.getData();

                subscriptionService.executeCallback(bot, chatId, messageId, callbackData);
            }
        });
    }
}
