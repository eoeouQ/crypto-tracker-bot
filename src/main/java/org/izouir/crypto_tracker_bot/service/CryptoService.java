package org.izouir.crypto_tracker_bot.service;

import org.izouir.crypto_tracker_bot.dto.CryptoCurrencyDto;
import org.izouir.crypto_tracker_bot.entity.User;
import org.izouir.crypto_tracker_bot.repository.UserRepository;
import org.izouir.crypto_tracker_bot.service.bot.BotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.izouir.crypto_tracker_bot.util.constant.service.AuthorizationServiceConstant.UNAUTHORIZED_ACCESS_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.*;

@Service
public class CryptoService {
    private final WebClient webClient;
    private final BotMessageService botMessageService;
    private final UserRepository userRepository;

    @Autowired
    public CryptoService(final WebClient webClient,
                         final BotMessageService botMessageService,
                         final UserRepository userRepository) {
        this.webClient = webClient;
        this.botMessageService = botMessageService;
        this.userRepository = userRepository;
    }

    public void sendTopCryptoCurrencies(final TelegramLongPollingBot bot, final Long chatId) {
        final Optional<User> user = userRepository.findById(chatId);
        if (user.isPresent()) {
            final List<CryptoCurrencyDto> cryptoState = pullCryptoState();
            final String text = buildTopCryptoCurrenciesText(cryptoState);
            botMessageService.sendAsync(bot, chatId, text);
        } else {
            botMessageService.sendAsync(bot, chatId, UNAUTHORIZED_ACCESS_MESSAGE);
        }
    }

    private String buildTopCryptoCurrenciesText(List<CryptoCurrencyDto> cryptoState) {
        cryptoState = cryptoState.stream()
                .sorted(Comparator.comparing(CryptoCurrencyDto::price).reversed())
                .toList();

        final StringBuilder cryptoStateStringBuilder = new StringBuilder();
        cryptoStateStringBuilder.append(String.format(TOP_CRYPTO_CURRENCIES_MESSAGE, TOP_CRYPTO_CURRENCIES_SIZE));
        for (int i = 0; i < TOP_CRYPTO_CURRENCIES_SIZE; i++) {
            final CryptoCurrencyDto cryptoCurrencyDto = cryptoState.get(i);
            cryptoStateStringBuilder.append(String.format(TOP_CRYPTO_CURRENCY_LINE_FORMAT,
                    i + 1, cryptoCurrencyDto.symbol(), cryptoCurrencyDto.price()));
        }

        return cryptoStateStringBuilder.toString();
    }

    private List<CryptoCurrencyDto> pullCryptoState() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(CryptoCurrencyDto.class)
                .collectList()
                .block();
    }
}
