package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.dto.TransferRequest;

public interface TransactionService {

    TransactionResponse deposit(TransactionRequest request);
    TransactionResponse transfer(TransferRequest request);
}
