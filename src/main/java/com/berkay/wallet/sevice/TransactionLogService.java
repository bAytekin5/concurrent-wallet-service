package com.berkay.wallet.sevice;


import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransferRequest;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.TransactionStatus;
import com.berkay.wallet.entity.enums.TransactionType;
import com.berkay.wallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionRepository repository;
    private static final String DEPOSIT_DESCRIPTION = "Deposit transaction";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction createPendingTransaction(Wallet wallet, TransactionRequest request) {

        Transaction transaction = Transaction.builder()
                .receiverWallet(wallet)
                .amount(request.amount())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .description(DEPOSIT_DESCRIPTION)
                .build();

        Transaction savedTransaction = this.repository.saveAndFlush(transaction);
        log.info("Pending transaction created with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction createPendingTransaction(TransferRequest request, Wallet walletSender, Wallet walletReceiver) {

        String senderIdStr = String.valueOf(walletSender.getId());
        String receiverIdStr = String.valueOf(walletReceiver.getId());

        String safeSenderId = truncateId(senderIdStr);
        String safeReceiverId = truncateId(receiverIdStr);

        Transaction transaction = Transaction.builder()
                .senderWallet(walletSender)
                .receiverWallet(walletReceiver)
                .amount(request.amount())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .description(String.format("Transfer from: %s to %s", safeSenderId, safeReceiverId))
                .build();

        Transaction savedTransaction = this.repository.saveAndFlush(transaction);
        log.info("Pending TRANSFER transaction created with ID: {}", savedTransaction.getId());

        return savedTransaction;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(UUID transactionId, String errorMessage) {

        Transaction transaction = this.repository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for ID: " + transactionId));

        transaction.setStatus(TransactionStatus.FAILED);

        this.repository.save(transaction);

        log.error("Transaction [{}] marked as FAILED. Reason: {}", transactionId, errorMessage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsSuccess(UUID transactionId) {

        Transaction transaction = this.repository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for ID: " + transactionId));

        transaction.setStatus(TransactionStatus.SUCCESS);
        this.repository.save(transaction);
        log.info("Transaction [{}] marked as SUCCESS.", transactionId);
    }

    private String truncateId(String id) {
        if (id != null && id.length() > 8) {
            return id.substring(0, 8);
        }
        return id;
    }
}
