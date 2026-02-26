package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.exception.GenericAlreadyExistsException;
import com.berkay.wallet.exception.UserNotFoundException;
import com.berkay.wallet.mapper.WalletMapper;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public WalletResponse createWallet(WalletCreateRequest request) {

        User user = this.userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(request.userId())));

        if (this.walletRepository.existsByUserIdAndCurrency(request.userId(), request.currency())) {
            throw new GenericAlreadyExistsException(request.currency().name());
        }

        Wallet entity = WalletMapper.toEntity(request);
        entity.setUser(user);
        entity = this.walletRepository.save(entity);
        return WalletMapper.toResponse(entity);
    }
}

