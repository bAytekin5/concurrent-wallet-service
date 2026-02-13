package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WalletCreateRequest(
        @NotNull(message = USER_ID_MESSAGE)
        UUID userId,
        @NotNull(message = CURRENCY_MESSAGE)
        Currency currency
) {
    private static final String USER_ID_MESSAGE = "User ID cannot be null";
    private static final String CURRENCY_MESSAGE = "Currency cannot be null";
}
