package com.berkay.wallet.exception;

import java.io.Serial;

public class WalletUpdateConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String errMsgKey;
    private final String errCode;

    public WalletUpdateConflictException(ErrorCode code) {
        super(code.getErrMsgKey());
        this.errMsgKey = code.getErrMsgKey();
        this.errCode = code.getErrCode();
    }

    public WalletUpdateConflictException(String message) {
        super(message);
        this.errMsgKey = ErrorCode.WALLET_UPDATE_CONFLICT.getErrMsgKey();
        this.errCode = ErrorCode.WALLET_UPDATE_CONFLICT.getErrCode();
    }
}
