package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.TransactionStatus;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.exception.WalletNotFoundException;
import com.berkay.wallet.exception.WalletUpdateConflictException;
import com.berkay.wallet.mapper.TransactionMapper;
import com.berkay.wallet.repository.TransactionRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.TransactionService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {

        Wallet wallet = this.walletRepository.findById(request.walletId()).orElseThrow(
                () -> new WalletNotFoundException(request.walletId())
        );

        try {
            wallet.setBalance(wallet.getBalance().add(request.amount()));

            Wallet updatedWallet = this.walletRepository.save(wallet);

            Transaction transaction = Transaction.builder()
                    .receiverWallet(updatedWallet)
                    .amount(request.amount())
                    .transactionType(TransactionType.DEPOSIT)
                    .status(TransactionStatus.SUCCESS)
                    .transactionDate(LocalDateTime.now())
                    .description("Deposit transaction")
                    .build();

            Transaction savedTransaction = this.transactionRepository.save(transaction);
            log.info("Deposit successful: walletId={}, amount={}", wallet.getId(), request.amount());

            return TransactionMapper.getTransactionResponse(savedTransaction, updatedWallet);
        } catch (OptimisticLockException ex) {
            log.warn("Concurrent update detected for walletId={}", wallet.getId());
            throw new WalletUpdateConflictException(wallet.getId());
        }

    }
}
