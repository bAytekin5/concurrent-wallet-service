package com.berkay.wallet.service;

import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.repository.TransactionRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.impl.TransactionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("You should deposit the money securely and update the balance.")
    void shouldDepositMoneySuccessfully() {
        // arr
        UUID walletId = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal depositAmount = BigDecimal.valueOf(500);

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(initialBalance);

        TransactionRequest request = new TransactionRequest(walletId, depositAmount);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = (Transaction) i.getArguments()[0];
            t.setId(UUID.randomUUID());
            return t;
        });

        // act
        TransactionResponse response = transactionService.deposit(request);

        // assert
        assertNotNull(response);
        assertEquals(TransactionType.DEPOSIT, response.type());

        assertEquals(BigDecimal.valueOf(600), response.newBalance());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        assertEquals(BigDecimal.valueOf(600), walletCaptor.getValue().getBalance());
    }
}
