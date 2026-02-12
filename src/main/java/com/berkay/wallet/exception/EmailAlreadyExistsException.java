package com.berkay.wallet.exception;

import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException(String detail) {
        super(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, detail));
    }
}
