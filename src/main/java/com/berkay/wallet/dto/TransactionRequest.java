package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = WALLET_ID_NOT_NULL)
        UUID walletId,

        @NotNull(message = AMOUNT_NOT_NULL)
        @DecimalMin(value = "0.01", message = AMOUNT_GREATER_THAN)
        BigDecimal amount,

        @NotNull(message = CURRENCY_NOT_NULL)
        Currency currency
) {

    private static final String WALLET_ID_NOT_NULL = "Wallet ID is mandatory";
    private static final String AMOUNT_NOT_NULL = "Amount is mandatory";
    private static final String AMOUNT_GREATER_THAN = "Amount must be greater then 0";
    private static final String CURRENCY_NOT_NULL = "Currency is mandatory";
}
