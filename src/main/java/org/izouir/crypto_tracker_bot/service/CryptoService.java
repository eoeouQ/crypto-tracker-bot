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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.izouir.crypto_tracker_bot.util.constant.WebClientConstant.USDT_SUFFIX;
import static org.izouir.crypto_tracker_bot.util.constant.service.AuthorizationServiceConstant.UNAUTHORIZED_ACCESS_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.MIN_CRYPTO_CURRENCY_PRICE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.TOP_CRYPTO_CURRENCIES_MESSAGE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.TOP_CRYPTO_CURRENCIES_SIZE;
import static org.izouir.crypto_tracker_bot.util.constant.service.CryptoServiceConstant.TOP_CRYPTO_CURRENCY_LINE_FORMAT;

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
        final Optional<User> optionalUser = userRepository.findById(chatId);
        if (optionalUser.isPresent()) {
            final Map<String, Double> cryptoState = pullCryptoState();
            final String text = buildTopCryptoCurrenciesText(cryptoState);
            botMessageService.sendAsync(bot, chatId, text);
        } else {
            botMessageService.sendAsync(bot, chatId, UNAUTHORIZED_ACCESS_MESSAGE);
        }
    }

    private String buildTopCryptoCurrenciesText(Map<String, Double> cryptoState) {
        cryptoState = cryptoState.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        final StringBuilder cryptoStateStringBuilder = new StringBuilder();
        cryptoStateStringBuilder.append(String.format(TOP_CRYPTO_CURRENCIES_MESSAGE, TOP_CRYPTO_CURRENCIES_SIZE));

        int i = 0;
        for (final Map.Entry<String, Double> cryptoCurrency : cryptoState.entrySet()) {
            cryptoStateStringBuilder.append(String.format(TOP_CRYPTO_CURRENCY_LINE_FORMAT,
                    i + 1, cryptoCurrency.getKey(), cryptoCurrency.getValue()));
            i++;
            if (i == TOP_CRYPTO_CURRENCIES_SIZE) {
                break;
            }
        }

        return cryptoStateStringBuilder.toString();
    }

    public Map<String, Double> pullCryptoState() {
        final Map<String, Double> cryptoState = new HashMap<>();
        final List<CryptoCurrencyDto> cryptoCurrencyDtos = Objects.requireNonNull(webClient.get()
                        .retrieve()
                        .bodyToFlux(CryptoCurrencyDto.class)
                        .collectList()
                        .map(cryptoCurrencyDtoList -> cryptoCurrencyDtoList.stream()
                                .filter(cryptoCurrencyDto -> cryptoCurrencyDto.symbol().endsWith(USDT_SUFFIX))
                                .filter(cryptoCurrencyDto -> cryptoCurrencyDto.price() >= MIN_CRYPTO_CURRENCY_PRICE))
                        .block())
                .toList();
        for (final CryptoCurrencyDto cryptoCurrencyDto : cryptoCurrencyDtos) {
            final String cryptoCurrencyCode = cryptoCurrencyDto.symbol().replaceAll(USDT_SUFFIX, "");
            cryptoState.put(cryptoCurrencyCode, cryptoCurrencyDto.price());
        }
        return cryptoState;
    }
}
