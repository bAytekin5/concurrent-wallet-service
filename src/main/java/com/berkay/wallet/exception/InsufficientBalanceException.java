package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class InsufficientBalanceException extends BaseException {
    public InsufficientBalanceException(String detail) {
        super(new ErrorMessage(MessageType.INSUFFICIENT_BALANCE, detail));
    }
}
