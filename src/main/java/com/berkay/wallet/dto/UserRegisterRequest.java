package com.berkay.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = USERNAME_NOT_BLANK_MESSAGE)
        @Size(min = 3, max = 50, message = USERNAME_SIZE_VALID_MESSAGE)
        String username,
        @Email(message = EMAIL_VALID_MESSAGE)
        @NotBlank(message = EMAIL_NOT_BLANK_MESSAGE)
        String email,
        @NotBlank(message = PASSWORD_NOT_BLANK_MESSAGE)
        @Size(min = 8, max = 255, message = PASSWORD_SIZE_VALID_MESSAGE)
        String password
) {

    private static final String USERNAME_NOT_BLANK_MESSAGE = "Name is mandatory";
    private static final String USERNAME_SIZE_VALID_MESSAGE = "Username must be between 3 and 50 characters";
    private static final String EMAIL_VALID_MESSAGE = "Email should be valid";
    private static final String EMAIL_NOT_BLANK_MESSAGE = "Email is mandatory";
    private static final String PASSWORD_NOT_BLANK_MESSAGE = "Password is mandatory";
    private static final String PASSWORD_SIZE_VALID_MESSAGE = "Password must be at least 8 characters";
}
