package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String detail) {
        super(new ErrorMessage(MessageType.USER_ID_NOT_FOUND, detail));
    }
}
