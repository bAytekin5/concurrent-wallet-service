package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException(String detail) {
        super(new ErrorMessage(MessageType.USERNAME_ALREADY_EXISTS, detail));
    }
}
