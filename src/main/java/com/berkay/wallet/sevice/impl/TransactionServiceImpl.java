package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.config.RabbitMQConfig;
import com.berkay.wallet.dto.*;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.TransactionStatus;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.exception.ResourceNotFoundException;
import com.berkay.wallet.exception.WalletNotFoundException;
import com.berkay.wallet.exception.WalletUpdateConflictException;
import com.berkay.wallet.mapper.TransactionMapper;
import com.berkay.wallet.repository.TransactionRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.TransactionService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig config;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "transactionHistory", allEntries = true)
    public TransactionResponse deposit(TransactionRequest request) {

        Wallet wallet = this.walletRepository.findById(request.walletId()).orElseThrow(
                () -> new WalletNotFoundException("Invalid wallet id")
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
            throw new WalletUpdateConflictException(String.valueOf(wallet.getId()));
        }

    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "transactionHistory", allEntries = true)
    public TransactionResponse transfer(TransferRequest request) {

        log.info("Transfer initiated: Sender [{}], Receiver [{}], Amount [{}]",
                request.senderWalletId(), request.receiverWalletId(), request.amount());

        Wallet walletSender = this.walletRepository.findById(request.senderWalletId()).orElseThrow(
                () -> {
                    log.error("Transfer failed: Sender wallet not found [{}]", request.receiverWalletId());
                    return new ResourceNotFoundException("Invalid wallet id");
                }
        );
        Wallet walletReceiver = this.walletRepository.findById(request.receiverWalletId()).orElseThrow(
                () -> {
                    log.warn("Transfer failed: Receiver wallet not found [{}]", walletSender.getId());
                    return new ResourceNotFoundException("Invalid wallet id");
                }
        );

        if (walletSender.getId().equals(walletReceiver.getId())) {
            // todo
            log.warn("Transfer failed: Sender and receiver wallets are the same [{}]", walletSender.getId());
            throw new ResourceNotFoundException("Invalid wallet id");
        }
        if (walletSender.getCurrency() != walletReceiver.getCurrency()) {
            log.warn("Transfer failed: Currency mismatch. Sender [{}], Receiver [{}]",
                    walletSender.getCurrency(), walletReceiver.getCurrency());
            throw new ResourceNotFoundException("Invalid wallet id");
        }
        if (walletSender.getBalance().compareTo(request.amount()) < 0) {
            log.warn("Transfer failed: Insufficient balance. Wallet [{}], Current Balance [{}], Requested Amount [{}]",
                    walletSender.getId(), walletSender.getBalance(), request.amount());
            throw new ResourceNotFoundException("Invalid wallet id");
        }

        walletSender.setBalance(walletSender.getBalance().subtract(request.amount()));
        walletReceiver.setBalance(walletReceiver.getBalance().add(request.amount()));

        this.walletRepository.save(walletSender);
        this.walletRepository.save(walletReceiver);

        TransferEvent event = new TransferEvent(walletSender.getId(), walletReceiver.getId(), request.amount());
        this.rabbitTemplate.convertAndSend(config.getTransferQueueName(), event);
        log.info("Message sent to RabbitMQ");

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

    @Override
    @Cacheable(cacheNames = "transactionHistory", key = "#walletId.toString() + '_' + #page")
    public Page<TransactionHistoryResponse> getHistory(UUID walletId, int page, int size) {

        log.info("Fetching history from database for wallet: {}", walletId);

        if (!this.walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException("Invalid wallet id" + walletId);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = this.transactionRepository.findAllByWalletId(walletId, pageable);

        return transactions.map(t -> new TransactionHistoryResponse(
                t.getId(),
                t.getSenderWallet() != null ? t.getSenderWallet().getId() : null,
                t.getReceiverWallet() != null ? t.getReceiverWallet().getId() : null,
                t.getAmount(),
                t.getTransactionType(),
                t.getTransactionDate(),
                t.getDescription()
        ));
    }
}
