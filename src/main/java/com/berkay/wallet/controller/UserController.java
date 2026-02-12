package com.berkay.wallet.controller;

import com.berkay.wallet.dto.UserRegisterRequest;
import com.berkay.wallet.dto.UserResponse;
import com.berkay.wallet.sevice.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.register(request));
    }
}
