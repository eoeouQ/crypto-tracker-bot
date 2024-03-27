package org.izouir.crypto_tracker_bot.scheduler;

import org.izouir.crypto_tracker_bot.dto.CryptoCurrencyDto;
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
import java.util.Objects;

import static org.izouir.crypto_tracker_bot.util.constant.scheduler.SubscriptionSchedulerConstant.NOTIFY_MESSAGE;

@Component
public class SubscriptionScheduler {
    private final TelegramLongPollingBot bot;
    private final BotMessageService botMessageService;
    private final CryptoService cryptoService;
    private final UserRepository userRepository;
    private final List<User> subscribers = new ArrayList<>();
    private List<CryptoCurrencyDto> cryptoState;

    @Autowired
    public SubscriptionScheduler(final TelegramLongPollingBot bot,
                                 final BotMessageService botMessageService,
                                 final CryptoService cryptoService,
                                 final UserRepository userRepository) {
        this.bot = bot;
        this.botMessageService = botMessageService;
        this.cryptoService = cryptoService;
        this.userRepository = userRepository;

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

    @Scheduled(cron = "* * 1 * * *")
    protected void loadCryptoState() {
        cryptoState = cryptoService.pullCryptoState();
    }

    @Scheduled(cron = "30 * * * * *")
    protected void notifySubscribers() {
        final List<CryptoCurrencyDto> actualCryptoState = cryptoService.pullCryptoState();
        for (final User subscriber : subscribers) {
            for (int i = 0; i < cryptoState.size(); i++) {
                final CryptoCurrencyDto currency = actualCryptoState.get(i);

                final Double price = cryptoState.get(i).price();
                final Double actualPrice = currency.price();
                final double difference = (actualPrice - price) / price;

                if (Math.abs(difference) >= subscriber.getSubscriberPercent()) {
                    final String notifyMessage = String.format(NOTIFY_MESSAGE,
                            currency.symbol(), currency.price(), difference);
                    botMessageService.sendAsync(bot, subscriber.getChatId(), notifyMessage);
                }
            }
        }
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
