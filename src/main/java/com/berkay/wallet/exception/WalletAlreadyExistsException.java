package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class WalletAlreadyExistsException extends BaseException {
    public WalletAlreadyExistsException(String detail) {
        super(new ErrorMessage(MessageType.WALLET_ALREADY_EXISTS, detail));
    }
}
