package com.berkay.wallet.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageType {
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "User Id not found"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    PASSWORD_TOO_WEAK(HttpStatus.BAD_REQUEST, "Password is too weak"),
    WALLET_ALREADY_EXISTS(HttpStatus.CONFLICT, "Wallet already exists for currency"),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "Wallet not found"),
    WALLET_CONFLICT(HttpStatus.CONFLICT, "Wallet could not be updated due to a conflicting transaction"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Insufficient balance");

    private final HttpStatus httpStatus;
    private final String message;
}
