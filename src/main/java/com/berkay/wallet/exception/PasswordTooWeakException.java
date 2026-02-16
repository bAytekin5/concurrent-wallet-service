package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class PasswordTooWeakException extends BaseException {
    public PasswordTooWeakException(String detail) {
        super(new ErrorMessage(MessageType.PASSWORD_TOO_WEAK, detail));
    }
}
