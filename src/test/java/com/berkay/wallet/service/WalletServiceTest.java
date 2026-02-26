package com.berkay.wallet.service;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.exception.GenericAlreadyExistsException;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.impl.WalletServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    @DisplayName("Should create wallet successfully when user exists and no wallet exists")
    void shouldCreateWallet_WhenUserExistsAndWalletDoesNot() {
        // arr
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        WalletCreateRequest request = new WalletCreateRequest(userId, Currency.TRY);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.existsByUserIdAndCurrency(userId, Currency.TRY)).thenReturn(false);

        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet wallet = invocation.getArgument(0);
            wallet.setId(UUID.randomUUID());
            return wallet;
        });

        // act
        Wallet response = walletService.createWallet(request);

        // assert
        assertNotNull(response);
        assertNotNull(response.getId(), "Kaydedilen cüzdanın ID'si null olmamalı");

        assertEquals(Currency.TRY, response.getCurrency());

        assertEquals(userId, response.getUser().getId());

        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(userRepository).findById(userId);
        verify(walletRepository).existsByUserIdAndCurrency(userId, Currency.TRY);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw exception when wallet already exists")
    void shouldThrowException_WhenWalletAlreadyExists() {
        // arr
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Currency currency = Currency.TRY;

        WalletCreateRequest request = new WalletCreateRequest(userId, currency);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(walletRepository.existsByUserIdAndCurrency(userId, Currency.TRY)).thenReturn(true);

        // act
        assertThrows(GenericAlreadyExistsException.class, () -> walletService.createWallet(request));

        verify(walletRepository).existsByUserIdAndCurrency(userId, currency);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
