package com.berkay.wallet.mapper;

import com.berkay.wallet.dto.UserRegisterRequest;
import com.berkay.wallet.dto.UserResponse;
import com.berkay.wallet.entity.User;

public class UserMapper {

    public static User toEntity(UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(request.password());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
