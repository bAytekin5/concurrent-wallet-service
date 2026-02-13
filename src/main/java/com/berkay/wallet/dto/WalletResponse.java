package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        UUID userId,
        Currency currency,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
