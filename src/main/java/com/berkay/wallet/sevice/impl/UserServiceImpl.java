package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.dto.UserRegisterRequest;
import com.berkay.wallet.dto.UserResponse;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.exception.EmailAlreadyExistsException;
import com.berkay.wallet.exception.UsernameAlreadyExistsException;
import com.berkay.wallet.mapper.UserMapper;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.sevice.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {

        if (this.userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        if (this.userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = UserMapper.toEntity(request);
        user.setPasswordHash(this.passwordEncoder.encode(user.getPasswordHash()));
        User save = this.userRepository.save(user);
        return UserMapper.toResponse(save);
    }
}
