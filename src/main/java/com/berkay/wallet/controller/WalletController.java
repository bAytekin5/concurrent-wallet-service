package com.berkay.wallet.controller;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.hateoas.WalletRepresentationModelAssembler;
import com.berkay.wallet.sevice.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable UUID id) {
        Wallet wallet = this.service.getWalletById(id);
        WalletResponse response = this.assembler.toModel(wallet);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CollectionModel<WalletResponse>> getWalletsByUserId(@PathVariable UUID userId) {
        List<Wallet> wallets = this.service.getWalletsByUserId(userId);
        CollectionModel<WalletResponse> walletResponses = this.assembler.toCollectionModel(wallets);
//        walletResponses.add(linkTo(methodOn(WalletController.class).getWalletsByUserId(userId)).withSelfRel());
        return ResponseEntity.ok(walletResponses);
    }

    @GetMapping("/user/{userId}/currency/{currency}")
    public ResponseEntity<WalletResponse> getWalletByUserIdAndCurrency(
            @PathVariable UUID userId,
            @PathVariable Currency currency
    ) {
        Wallet wallet = this.service.getWalletByUserIdAndCurrency(userId, currency);
        WalletResponse response = this.assembler.toModel(wallet);
        return ResponseEntity.ok(response);
    }
}
