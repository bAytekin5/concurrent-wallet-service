package com.berkay.wallet.service;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.exception.BaseException;
import com.berkay.wallet.exception.WalletAlreadyExistsException;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.WalletService;
import com.berkay.wallet.sevice.impl.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        User user = new User();
        UUID userId = UUID.randomUUID();
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
        WalletResponse response = walletService.createWallet(request);

        assertNotNull(response);
        assertEquals(Currency.TRY, response.currency());
        assertEquals(userId, response.userId());

        verify(userRepository).findById(userId);
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
        assertThrows(WalletAlreadyExistsException.class, () -> walletService.createWallet(request));

        verify(walletRepository).existsByUserIdAndCurrency(userId, currency);
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
