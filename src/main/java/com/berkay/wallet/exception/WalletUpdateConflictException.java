package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

import java.util.UUID;

public class WalletUpdateConflictException extends BaseException {
    public WalletUpdateConflictException(UUID walletId) {
        super(new ErrorMessage(MessageType.WALLET_CONFLICT, "Wallet Id: " + walletId));
    }
}
