package com.berkay.wallet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    GENERIC_ERROR("WALL-0001", "The system is unable to complete the request. Contact system support."),
    HTTP_MEDIATYPE_NOT_SUPPORTED("WALL-0002", "Requested media type is not supported. Please use application/json or application/xml as 'Content-Type' header value"),
    HTTP_MESSAGE_NOT_WRITABLE("WALL-0003", "Missing 'Accept' header. Please add 'Accept' header."),
    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE("WALL-0004", "Requested 'Accept' header value is not supported. Please use application/json or application/xml as 'Accept' value"),
    JSON_PARSE_ERROR("WALL-0005", "Make sure request payload should be a valid JSON object."),
    HTTP_MESSAGE_NOT_READABLE("WALL-0006", "Make sure request payload should be a valid JSON or XML object according to 'Content-Type'."),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED("WALL-0007", "Request method not supported."),
    CONSTRAINT_VIOLATION("WALL-0008", "Validation failed."),
    ILLEGAL_ARGUMENT_EXCEPTION("WALL-0009", "Invalid data passed."),
    RESOURCE_NOT_FOUND("WALL-0010", "Requested resource not found"),
    USER_NOT_FOUND("WALL-0011", "Requested user not found"),
    WALLET_NOT_FOUND("WALL-0012", "Requested wallet not found"),
    GENERIC_ALREADY_EXISTS("WALL-0013", "Already exists."),
    WALLET_UPDATE_CONFLICT("WALL-0014", "Concurrent update detected. Please try again later.");


    private final String errCode;
    private final String errMsgKey;

    ErrorCode(String errCode, String errMsgKey) {
        this.errCode = errCode;
        this.errMsgKey = errMsgKey;
    }
}

