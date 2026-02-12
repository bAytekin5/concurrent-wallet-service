package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.UserRegisterRequest;
import com.berkay.wallet.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRegisterRequest request);
}
