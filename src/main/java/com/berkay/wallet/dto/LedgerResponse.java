package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.entity.enums.LedgerEntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerResponse(
        UUID id,
        UUID transactionId,
        LedgerEntryType entryType,
        BigDecimal amount,
        Currency currency,
        LocalDateTime createdAt
) {

}
