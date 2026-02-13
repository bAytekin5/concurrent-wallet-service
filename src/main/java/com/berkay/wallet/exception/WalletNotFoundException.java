package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

import java.util.UUID;

public class WalletNotFoundException extends BaseException {
    public WalletNotFoundException(UUID walletId) {
        super(new ErrorMessage(MessageType.WALLET_NOT_FOUND, "Wallet id: " + walletId));
    }
}
