package com.berkay.wallet.exception;

public class ErrorUtils {

    private ErrorUtils() {
    }

    public static ApiError createError(String errMsgKey, String errorCode, Integer httpStatusCode) {
        ApiError error = new ApiError();
        error.setMessage(errMsgKey);
        error.setErrCode(errorCode);
        error.setStatus(httpStatusCode);
        return error;
    }
}
