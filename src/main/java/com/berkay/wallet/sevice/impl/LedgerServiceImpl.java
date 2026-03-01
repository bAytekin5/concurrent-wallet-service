package com.berkay.wallet.sevice.impl;

import com.berkay.wallet.entity.LedgerEntry;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.entity.enums.LedgerEntryType;
import com.berkay.wallet.repository.LedgerRepository;
import com.berkay.wallet.sevice.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final LedgerRepository ledgerRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createDoubleEntry(Transaction transaction, UUID debitWalletId, UUID creditWalletId, BigDecimal amount, Currency currency) {

        LedgerEntry debit = LedgerEntry.builder()
                .transaction(transaction)
                .walletId(debitWalletId)
                .entryType(LedgerEntryType.DEBIT)
                .currency(currency)
                .build();

        LedgerEntry credit = LedgerEntry.builder()
                .transaction(transaction)
                .walletId(creditWalletId)
                .entryType(LedgerEntryType.CREDIT)
                .currency(currency)
                .build();

        ledgerRepository.saveAll(List.of(debit, credit));
        log.info("Ledger entries created for transactions: {}", transaction.getId());
    }


}
