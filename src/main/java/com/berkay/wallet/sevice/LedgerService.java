package com.berkay.wallet.sevice;

import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerService {
    void createDoubleEntry(Transaction transaction, UUID debitWalletId, UUID creditWalletId, BigDecimal amount, Currency currency);

}
