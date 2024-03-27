package org.izouir.crypto_tracker_bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CryptoCurrencyDto(@NotBlank String symbol,
                                @NotNull Double price) {
}
