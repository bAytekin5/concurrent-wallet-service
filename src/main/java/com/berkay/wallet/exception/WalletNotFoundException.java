package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class WalletNotFoundException extends BaseException {
    public WalletNotFoundException(String detail) {
        super(new ErrorMessage(MessageType.WALLET_NOT_FOUND, detail));
    }
}
