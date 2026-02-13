package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse deposit(TransactionRequest request);
}
