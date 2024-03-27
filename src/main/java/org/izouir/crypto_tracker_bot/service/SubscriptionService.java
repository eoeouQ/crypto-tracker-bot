package org.izouir.crypto_tracker_bot.service;

import org.izouir.crypto_tracker_bot.entity.User;
import org.izouir.crypto_tracker_bot.keyboard.service.SubscriptionServiceKeyboard;
import org.izouir.crypto_tracker_bot.repository.UserRepository;
import org.izouir.crypto_tracker_bot.scheduler.SubscriptionScheduler;
import org.izouir.crypto_tracker_bot.service.bot.BotMessageService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

import static org.izouir.crypto_tracker_bot.util.constant.repository.UserRepositoryConstant.USER_WITH_CHAT_ID_WAS_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.AuthorizationServiceConstant.UNAUTHORIZED_ACCESS_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.SubscriptionServiceConstant.*;

@Service
public class SubscriptionService {
    private final SubscriptionServiceKeyboard subscriptionServiceKeyboard;
    private final SubscriptionScheduler subscriptionScheduler;
    private final BotMessageService botMessageService;
    private final UserRepository userRepository;

    public SubscriptionService(final SubscriptionServiceKeyboard subscriptionServiceKeyboard,
                               @Lazy final SubscriptionScheduler subscriptionScheduler,
                               final BotMessageService botMessageService,
                               final UserRepository userRepository) {
        this.subscriptionServiceKeyboard = subscriptionServiceKeyboard;
        this.subscriptionScheduler = subscriptionScheduler;
        this.botMessageService = botMessageService;
        this.userRepository = userRepository;
    }

    public void executeCallback(final TelegramLongPollingBot bot, final Long chatId,
                                final Integer messageId, final String callbackData) {
        switch (callbackData) {
            case LOW_SUBSCRIBER_PERCENT_CALLBACK_DATA -> {
                final User user = userRepository.findById(chatId).orElseThrow(
                        () -> new RuntimeException(String.format(USER_WITH_CHAT_ID_WAS_NOT_FOUND_EXCEPTION_MESSAGE, chatId))
                );
                subscribe(bot, user, LOW_SUBSCRIBER_PERCENT_VALUE);
                botMessageService.deleteAsync(bot, chatId, messageId);
            }
            case MEDIUM_SUBSCRIBER_PERCENT_CALLBACK_DATA -> {
                final User user = userRepository.findById(chatId).orElseThrow(
                        () -> new RuntimeException(String.format(USER_WITH_CHAT_ID_WAS_NOT_FOUND_EXCEPTION_MESSAGE, chatId))
                );
                subscribe(bot, user, MEDIUM_SUBSCRIBER_PERCENT_VALUE);
                botMessageService.deleteAsync(bot, chatId, messageId);
            }
            case HIGH_SUBSCRIBER_PERCENT_CALLBACK_DATA -> {
                final User user = userRepository.findById(chatId).orElseThrow(
                        () -> new RuntimeException(String.format(USER_WITH_CHAT_ID_WAS_NOT_FOUND_EXCEPTION_MESSAGE, chatId))
                );
                subscribe(bot, user, HIGH_SUBSCRIBER_PERCENT_VALUE);
                botMessageService.deleteAsync(bot, chatId, messageId);
            }
        }
    }

    public void subscribe(final TelegramLongPollingBot bot, final Long chatId) {
        final Optional<User> optionalUser = userRepository.findById(chatId);
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (!user.isSubscriber()) {
                if (subscriptionScheduler.getSubscribersPoolSize() < MAX_SUBSCRIBERS_POOL_SIZE) {
                    final SendMessage message = SendMessage.builder()
                            .chatId(chatId)
                            .text(SUBSCRIBER_PERCENT_CHOICE_MESSAGE)
                            .replyMarkup(subscriptionServiceKeyboard.buildSubscriberPercentChoiceKeyboard())
                            .build();
                    botMessageService.sendMessageAsync(bot, message);
                } else {
                    botMessageService.sendAsync(bot, chatId, SUBSCRIBERS_POOL_OVERLOAD_MESSAGE);
                }
            } else {
                botMessageService.sendAsync(bot, chatId, ALREADY_SUBSCRIBER_MESSAGE);
            }
        } else {
            botMessageService.sendAsync(bot, chatId, UNAUTHORIZED_ACCESS_MESSAGE);
        }
    }

    public void unsubscribe(final TelegramLongPollingBot bot, final Long chatId) {
        final Optional<User> optionalUser = userRepository.findById(chatId);
        if (optionalUser.isPresent()) {
            final User user = optionalUser.get();
            if (user.isSubscriber()) {
                user.setIsSubscriber(false);
                user.setSubscriberPercent(null);
                userRepository.save(user);
                subscriptionScheduler.removeSubscriber(chatId);
                botMessageService.sendAsync(bot, chatId, UNSUBSCRIBE_SUCCESS_MESSAGE);
            } else {
                botMessageService.sendAsync(bot, chatId, ALREADY_NOT_SUBSCRIBER_MESSAGE);
            }
        } else {
            botMessageService.sendAsync(bot, chatId, UNAUTHORIZED_ACCESS_MESSAGE);
        }
    }

    private void subscribe(final TelegramLongPollingBot bot, final User user, final Integer subscriberPercent) {
        final Long chatId = user.getChatId();

        if (subscriptionScheduler.getSubscribersPoolSize() < MAX_SUBSCRIBERS_POOL_SIZE) {
            user.setIsSubscriber(true);
            user.setSubscriberPercent((float) (1.0 * subscriberPercent / 100));
            userRepository.save(user);
            subscriptionScheduler.addSubscriber(user);
            botMessageService.sendAsync(bot, chatId, SUBSCRIBE_SUCCESS_MESSAGE);
        } else {
            botMessageService.sendAsync(bot, chatId, SUBSCRIBERS_POOL_OVERLOAD_MESSAGE);
        }

    }
}
