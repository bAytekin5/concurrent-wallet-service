package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.dto.TransferRequest;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.TransactionStatus;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.exception.BaseException;
import com.berkay.wallet.exception.WalletNotFoundException;
import com.berkay.wallet.exception.WalletUpdateConflictException;
import com.berkay.wallet.exception.model.ErrorMessage;
import com.berkay.wallet.exception.model.MessageType;
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

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        log.info("Transfer initiated: Sender [{}], Receiver [{}], Amount [{}]",
                request.senderWalletId(), request.receiverWalletId(), request.amount());

        Wallet walletSender = this.walletRepository.findById(request.senderWalletId()).orElseThrow(
                () -> new WalletNotFoundException(request.senderWalletId())
        );
        Wallet walletReceiver = this.walletRepository.findById(request.receiverWalletId()).orElseThrow(
                () -> new WalletNotFoundException(request.receiverWalletId())
        );

        if (walletSender.getId().equals(walletReceiver.getId())) {
            // todo
            throw new BaseException(new ErrorMessage(MessageType.WALLET_CONFLICT, "Wallet ID's cannot be equals"));
        }
        if (walletSender.getCurrency() != walletReceiver.getCurrency()) {
            throw new BaseException(new ErrorMessage(MessageType.WALLET_CONFLICT, "Currencies should be equal"));
        }
        if (walletSender.getBalance().compareTo(request.amount()) < 0) {
            throw new BaseException(new ErrorMessage(MessageType.INSUFFICIENT_BALANCE, ""));
        }

        walletSender.setBalance(walletSender.getBalance().subtract(request.amount()));
        walletReceiver.setBalance(walletReceiver.getBalance().add(request.amount()));

        this.walletRepository.save(walletSender);
        this.walletRepository.save(walletReceiver);

        Transaction transaction = Transaction.builder()
                .senderWallet(walletSender)
                .receiverWallet(walletReceiver)
                .amount(request.amount())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .transactionDate(LocalDateTime.now())
                .description("Transfer from: " + walletSender.getId().toString().substring(0, 8) + " to " + walletReceiver.getId().toString().substring(0, 8))
                .build();
        Transaction save = this.transactionRepository.save(transaction);

        log.info("Transfer successful: Transaction ID [{}], Sender New Balance [{}]",
                save.getId(), walletSender.getBalance());

        return TransactionMapper.getTransactionResponse(save, walletSender);
    }
}
