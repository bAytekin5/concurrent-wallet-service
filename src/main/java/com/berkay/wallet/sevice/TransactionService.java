package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.TransactionHistoryResponse;
import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.dto.TransferRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse deposit(TransactionRequest request);
    TransactionResponse transfer(TransferRequest request);
    Page<TransactionHistoryResponse> getHistory(UUID walletId, int page, int size);
}
