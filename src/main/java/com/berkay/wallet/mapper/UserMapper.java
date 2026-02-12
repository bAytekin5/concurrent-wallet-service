package com.berkay.wallet.mapper;

import com.berkay.wallet.dto.UserRegisterRequest;
import com.berkay.wallet.dto.UserResponse;
import com.berkay.wallet.entity.User;

public class UserMapper {

    public static User toEntity(UserRegisterRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(request.password())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
