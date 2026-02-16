package com.berkay.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferEvent(
        UUID senderWalletId,
        UUID receiverWalletId,
        BigDecimal amount
) {
}
