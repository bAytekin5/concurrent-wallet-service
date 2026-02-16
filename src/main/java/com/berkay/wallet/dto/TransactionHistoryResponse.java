package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionHistoryResponse(
        UUID transactionId,
        UUID senderWalletId,
        UUID receiverWalletId,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime transactionDate,
        String description
) {
}
