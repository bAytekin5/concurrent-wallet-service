package com.berkay.wallet.repository;

import com.berkay.wallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderWallet.id = :walletId OR t.receiverWallet.id = :walletId " +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllByWalletId(@Param("walletId") UUID walletId, Pageable pageable);
}
