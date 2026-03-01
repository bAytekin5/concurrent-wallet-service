package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.entity.enums.TransactionStatus;
import com.berkay.wallet.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        UUID walletId,
        BigDecimal amount,
        BigDecimal newBalance,
        Currency currency,
        TransactionType type,
        TransactionStatus status,
        LocalDateTime transactionDate
) {
}
