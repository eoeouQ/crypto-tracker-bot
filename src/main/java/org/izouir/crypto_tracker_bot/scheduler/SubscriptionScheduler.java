package org.izouir.crypto_tracker_bot.scheduler;

import org.izouir.crypto_tracker_bot.entity.User;
import org.izouir.crypto_tracker_bot.repository.UserRepository;
import org.izouir.crypto_tracker_bot.service.CryptoService;
import org.izouir.crypto_tracker_bot.service.bot.BotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.izouir.crypto_tracker_bot.util.constant.scheduler.SubscriptionSchedulerConstant.NEGATIVE_NOTIFY_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.scheduler.SubscriptionSchedulerConstant.POSITIVE_NOTIFY_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.INVALID_CRYPTO_CURRENCY_PRICE;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.MAX_SUBSCRIBERS_POOL_SIZE;

@Component
public class SubscriptionScheduler {
    private final TelegramLongPollingBot bot;
    private final BotMessageService botMessageService;
    private final CryptoService cryptoService;
    private final UserRepository userRepository;
    private final List<User> subscribers = new ArrayList<>(MAX_SUBSCRIBERS_POOL_SIZE);
    private Map<String, Double> lastCryptoState;

    @Autowired
    public SubscriptionScheduler(final TelegramLongPollingBot bot,
                                 final BotMessageService botMessageService,
                                 final CryptoService cryptoService,
                                 final UserRepository userRepository) {
        this.bot = bot;
        this.botMessageService = botMessageService;
        this.cryptoService = cryptoService;
        this.userRepository = userRepository;

        lastCryptoState = cryptoService.pullCryptoState();
        loadSubscribersFromDatabase();
    }

    public void addSubscriber(final User user) {
        subscribers.add(user);
    }

    public void removeSubscriber(final Long chatId) {
        subscribers.removeIf(subscriber -> Objects.equals(subscriber.getChatId(), chatId));
    }

    public int getSubscribersPoolSize() {
        return subscribers.size();
    }

    @Scheduled(cron = "0 */20 * * * *")
    protected void notifySubscribers() {
        final Map<String, Double> actualCryptoState = cryptoService.pullCryptoState();

        for (final User subscriber : subscribers) {
            for (final Map.Entry<String, Double> actualCryptoCurrency : actualCryptoState.entrySet()) {
                final Double lastPrice = lastCryptoState.getOrDefault(actualCryptoCurrency.getKey(),
                        INVALID_CRYPTO_CURRENCY_PRICE);
                if (Objects.equals(lastPrice, INVALID_CRYPTO_CURRENCY_PRICE)) {
                    continue;
                }
                final Double actualPrice = actualCryptoCurrency.getValue();
                final double difference = (actualPrice - lastPrice) / lastPrice;

                if (Math.abs(difference) >= subscriber.getSubscriberPercent()) {
                    final String messageFormat;
                    if (difference > 0) {
                        messageFormat = POSITIVE_NOTIFY_MESSAGE;
                    } else {
                        messageFormat = NEGATIVE_NOTIFY_MESSAGE;
                    }

                    final String notifyMessage = String.format(messageFormat,
                            actualCryptoCurrency.getKey(), lastPrice, actualPrice, Math.abs(100 * difference));
                    botMessageService.sendAsync(bot, subscriber.getChatId(), notifyMessage);
                }
            }
        }

        lastCryptoState = actualCryptoState;
    }

    private void loadSubscribersFromDatabase() {
        final List<User> users = userRepository.findAll();
        for (final User user : users) {
            if (user.isSubscriber()) {
                addSubscriber(user);
            }
        }
    }
}
