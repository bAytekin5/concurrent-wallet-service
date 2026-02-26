package com.berkay.wallet.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class WalletNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String errMsgKey;
    private final String errCode;

    public WalletNotFoundException(ErrorCode code) {
        super(code.getErrMsgKey());
        this.errMsgKey = code.getErrMsgKey();
        this.errCode = code.getErrCode();
    }

    public WalletNotFoundException(String message) {
        super(message);
        this.errMsgKey = ErrorCode.WALLET_NOT_FOUND.getErrMsgKey();
        this.errCode = ErrorCode.WALLET_NOT_FOUND.getErrCode();
    }
}
