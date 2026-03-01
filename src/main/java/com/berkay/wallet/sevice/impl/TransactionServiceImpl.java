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
import com.berkay.wallet.sevice.LedgerService;
import com.berkay.wallet.sevice.TransactionLogService;
import com.berkay.wallet.sevice.TransactionService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final TransactionLogService logService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig config;

    @Value(value = "${app.finance.system.account-id}")
    private UUID systemAccountId;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "transactionHistory", allEntries = true)
    public TransactionResponse deposit(TransactionRequest request) {

        Wallet wallet = this.walletRepository.findById(request.walletId()).orElseThrow(
                () -> new WalletNotFoundException("Invalid wallet id: " + request.walletId())
        );

        if (!wallet.getCurrency().equals(request.currency())) {
            throw new IllegalArgumentException("Currency mismatch excepted: " + wallet.getCurrency());
        }

        Transaction savedTransaction = this.logService.createPendingTransaction(wallet, request);

        try {
            this.ledgerService.createDoubleEntry(
                    savedTransaction,
                    getSystemAccountId(),
                    wallet.getId(),
                    request.amount(),
                    request.currency()
            );
            wallet.setBalance(wallet.getBalance().add(request.amount()));
            this.walletRepository.save(wallet);

            savedTransaction.setStatus(TransactionStatus.SUCCESS);
            log.info("Deposit successful: walletId={}, amount={}, transactionId={}",
                    wallet.getId(), request.amount(), savedTransaction.getId());
            return TransactionMapper.getTransactionResponse(savedTransaction, wallet);
        } catch (Exception e) {
            this.logService.markAsFailed(savedTransaction.getId(), e.getMessage());
            log.error("error during deposit execution. Transaction rolled back." + e);
            throw e;
        }
    }

    @Override
    @Transactional(noRollbackFor = {IllegalArgumentException.class, IllegalStateException.class})
    @CacheEvict(cacheNames = "transactionHistory", allEntries = true)
    public TransactionResponse transfer(TransferRequest request) {

        log.info("Transfer initiated: Sender [{}], Receiver [{}], Amount [{}]",
                request.senderWalletId(), request.receiverWalletId(), request.amount());

        if (request.senderWalletId().equals(request.receiverWalletId())) {
            throw new IllegalArgumentException("Sender and receiver wallets cannot be the same");
        }
        Wallet walletSender = this.walletRepository.findById(request.senderWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found: " + request.senderWalletId()));

        Wallet walletReceiver = this.walletRepository.findById(request.receiverWalletId()).orElseThrow(
                () -> new WalletNotFoundException("Receiver wallet not found " + request.receiverWalletId()));

        if (!walletSender.getCurrency().equals(walletReceiver.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch between sender and receiver wallets");
        }

        Transaction transaction = this.logService.createPendingTransaction(request, walletSender, walletReceiver);

        if (walletSender.getBalance().compareTo(request.amount()) < 0) {
            this.logService.markAsFailed(transaction.getId(), "Insufficient balance");
            log.warn("Transfer failed: Insufficient balance. Wallet [{}], Current Balance [{}], Requested Amount [{}]",
                    walletSender.getId(), walletSender.getBalance(), request.amount());
            throw new IllegalStateException("Insufficient balance in sender wallet");
        }
        try {
            this.ledgerService.createDoubleEntry(
                    transaction,
                    walletSender.getId(),
                    walletReceiver.getId(),
                    request.amount(),
                    request.currency()
            );
            walletSender.setBalance(walletSender.getBalance().subtract(request.amount()));
            walletReceiver.setBalance(walletReceiver.getBalance().add(request.amount()));
            this.walletRepository.saveAll(List.of(walletSender, walletReceiver));

            transaction.setStatus(TransactionStatus.SUCCESS);

            // todo -- send the message to RabbitMQ only if the database is successfully committed
            TransferEvent event = new TransferEvent(walletSender.getId(), walletReceiver.getId(), request.amount());
            this.rabbitTemplate.convertAndSend(config.getTransferQueueName(), event);
            log.info("Message sent to RabbitMQ");

            log.info("Transfer successful: Transaction ID [{}], Sender New Balance [{}]",
                    transaction.getId(), walletSender.getBalance());

            return TransactionMapper.getTransactionResponse(transaction, walletSender);
        } catch (Exception e) {
            log.error("error during transfer execution. Transaction rolled back.", e);
            throw e;
        }
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

    private UUID getSystemAccountId() {
        return this.systemAccountId;
    }
}
