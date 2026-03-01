package com.berkay.wallet.controller;

import com.berkay.wallet.dto.TransactionHistoryResponse;
import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.dto.TransactionResponse;
import com.berkay.wallet.dto.TransferRequest;
import com.berkay.wallet.sevice.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class

TransactionController {

    private final TransactionService service;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(this.service.deposit(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(this.service.transfer(request));
    }

    @GetMapping("/history/{walletId}")
    public ResponseEntity<Page<TransactionHistoryResponse>> getTransactionHistory(@PathVariable UUID walletId,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(this.service.getHistory(walletId, page, size));
    }
}
