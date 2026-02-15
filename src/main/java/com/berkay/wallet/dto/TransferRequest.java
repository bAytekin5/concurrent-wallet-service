package com.berkay.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull(message = SENDER_WALLET_ID_MESSAGE)
        UUID senderWalletId,
        @NotNull(message = RECEIVER_WALLET_ID_MESSAGE)
        UUID receiverWalletId,

        @NotNull(message = AMOUNT_NOT_NULL_MESSAGE)
        @DecimalMin(value = "0.01", message = AMOUNT_MIN_MESSAGE)
        BigDecimal amount
) {

    private static final String SENDER_WALLET_ID_MESSAGE = "Sender Wallet Id is mandatory";
    private static final String RECEIVER_WALLET_ID_MESSAGE = "Receiver Wallet Id is mandatory";
    private static final String AMOUNT_NOT_NULL_MESSAGE = "Amount must not be null";
    private static final String AMOUNT_MIN_MESSAGE = "Amount must be greater than 0";
}
