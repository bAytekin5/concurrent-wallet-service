package com.berkay.wallet.repository;

import com.berkay.wallet.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findAllByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
