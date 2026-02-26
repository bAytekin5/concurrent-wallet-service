package com.berkay.wallet.service;

import com.berkay.wallet.dto.TransactionHistoryResponse;
import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.dto.TransferRequest;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("Should transfer money successfully between two different wallets")
    void shouldTransferMoneySuccessfully() {
        // arr
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal transferAmount = BigDecimal.valueOf(50);

        Wallet senderWallet = new Wallet();
        senderWallet.setId(senderId);
        senderWallet.setCurrency(Currency.TRY);
        senderWallet.setBalance(BigDecimal.valueOf(100));

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(receiverId);
        receiverWallet.setCurrency(Currency.TRY);
        receiverWallet.setBalance(BigDecimal.valueOf(20));

        TransferRequest transferRequest = new TransferRequest(senderId, receiverId, transferAmount);

        when(walletRepository.findById(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(receiverId)).thenReturn(Optional.of(receiverWallet));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        // act
        TransactionResponse response = transactionService.transfer(transferRequest);

        // assert
        assertNotNull(response);
        assertEquals(TransactionType.TRANSFER, response.type());

        assertEquals(BigDecimal.valueOf(50), senderWallet.getBalance());
        assertEquals(BigDecimal.valueOf(70), receiverWallet.getBalance());

        verify(walletRepository, times(1)).save(senderWallet);
        verify(walletRepository, times(1)).save(receiverWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when sender has insufficient balance")
    void shouldThrowException_WhenInsufficientBalance() {
        // arr
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Wallet senderWallet = new Wallet();
        senderWallet.setId(senderId);
        senderWallet.setCurrency(Currency.TRY);
        senderWallet.setBalance(BigDecimal.valueOf(10));

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(receiverId);
        receiverWallet.setCurrency(Currency.TRY);
        receiverWallet.setBalance(BigDecimal.ZERO);
        TransferRequest request = new TransferRequest(senderId, receiverId, BigDecimal.valueOf(100));

        when(walletRepository.findById(senderId)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(receiverId)).thenReturn(Optional.of(receiverWallet));

        // act, assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transfer(request));

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when transferring to the same wallet")
    void shouldThrowException_WhenSenderAndReceiverAreSame() {
        // arr
        UUID sameWalletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(sameWalletId);
        wallet.setCurrency(Currency.TRY);
        wallet.setBalance(BigDecimal.valueOf(100));

        TransferRequest request = new TransferRequest(sameWalletId, sameWalletId, BigDecimal.valueOf(50));

        when(walletRepository.findById(sameWalletId)).thenReturn(Optional.of(wallet));

        // act, assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.transfer(request));

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should return paginated transaction history")
    void shouldReturnPaginatedTransactionHistory() {
        // arr
        UUID walletId = UUID.randomUUID();
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTransactionType(TransactionType.DEPOSIT);


        List<Transaction> transactionList = List.of(transaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactionList, pageable, transactionList.size());

        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(transactionRepository.findAllByWalletId(walletId, pageable)).thenReturn(transactionPage);

        // act
        Page<TransactionHistoryResponse> result = transactionService.getHistory(walletId, page, size);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BigDecimal.valueOf(100), result.getContent().get(0).amount());

        verify(walletRepository).existsById(walletId);
        verify(transactionRepository).findAllByWalletId(walletId, pageable);
    }
}
