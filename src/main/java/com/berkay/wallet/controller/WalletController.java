package com.berkay.wallet.controller;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.hateoas.WalletRepresentationModelAssembler;
import com.berkay.wallet.mapper.WalletMapper;
import com.berkay.wallet.sevice.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;
    private final WalletRepresentationModelAssembler assembler;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletCreateRequest request) {
        Wallet wallet = this.service.createWallet(request);
        WalletResponse response = assembler.toModel(wallet);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable String id) {
        Wallet wallet = this.service.getWalletById(id);
        WalletResponse response = WalletMapper.toResponse(wallet);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
