package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class WalletUpdateConflictException extends BaseException {
    public WalletUpdateConflictException(String detail) {
        super(new ErrorMessage(MessageType.WALLET_CONFLICT, detail));
    }
}
