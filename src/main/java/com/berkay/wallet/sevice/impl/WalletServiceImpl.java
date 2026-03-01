package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.exception.GenericAlreadyExistsException;
import com.berkay.wallet.exception.UserNotFoundException;
import com.berkay.wallet.exception.WalletNotFoundException;
import com.berkay.wallet.mapper.WalletMapper;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public Wallet createWallet(WalletCreateRequest request) {

        User user = this.userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.userId()));

        if (this.walletRepository.existsByUserIdAndCurrency(request.userId(), request.currency())) {
            throw new GenericAlreadyExistsException(request.currency().name());
        }

        Wallet entity = WalletMapper.toEntity(request);
        entity.setBalance(BigDecimal.ZERO);
        user.addWallets(entity);
        return this.walletRepository.save(entity);
    }

    @Override
    public Wallet getWalletById(UUID id) {
        return this.walletRepository.findById(id).orElseThrow(
                () -> new WalletNotFoundException("Wallet not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByUserIdAndCurrency(UUID userId, Currency currency) {
        return this.walletRepository.findByUserIdAndCurrency(userId, currency).orElseThrow(
                () -> new WalletNotFoundException("Wallet not found!")
        );
    }

    @Override
    public List<Wallet> getWalletsByUserId(UUID userId) {
        return this.walletRepository.findAllByUserId(userId);
    }
}

