package com.berkay.wallet.mapper;

import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.entity.Transaction;
import com.berkay.wallet.entity.Wallet;
import org.jspecify.annotations.NonNull;

public class TransactionMapper {


    public static TransactionResponse getTransactionResponse(Transaction savedTransaction, Wallet updatedWallet) {
        return new TransactionResponse(
                savedTransaction.getId(),
                updatedWallet.getId(),
                savedTransaction.getAmount(),
                updatedWallet.getBalance(),
                savedTransaction.getCurrency(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getTransactionDate()
        );
    }

}
