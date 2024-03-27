package org.izouir.crypto_tracker_bot.service;

import org.izouir.crypto_tracker_bot.entity.User;
import org.izouir.crypto_tracker_bot.repository.UserRepository;
import org.izouir.crypto_tracker_bot.service.bot.BotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Optional;

import static org.izouir.crypto_tracker_bot.util.constant.service.AuthorizationServiceConstant.WELCOME_MESSAGE;

@Service
public class AuthorizationService {
    private final BotMessageService botMessageService;
    private final UserRepository userRepository;

    @Autowired
    public AuthorizationService(final BotMessageService botMessageService,
                                final UserRepository userRepository) {
        this.botMessageService = botMessageService;
        this.userRepository = userRepository;
    }

    public void authorizeUser(final TelegramLongPollingBot bot, final Long chatId, final String username) {
        final Optional<User> user = userRepository.findById(chatId);
        if (user.isPresent()) {
            botMessageService.sendAsync(bot, chatId, String.format(WELCOME_MESSAGE, user.get().getUsername()));
        } else {
            final User newUser = new User(chatId, username);
            userRepository.save(newUser);
            botMessageService.sendAsync(bot, chatId, String.format(WELCOME_MESSAGE, username));
        }
    }
}
