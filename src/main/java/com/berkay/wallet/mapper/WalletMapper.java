package com.berkay.wallet.mapper;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;

import java.math.BigDecimal;

public class WalletMapper {

    public static Wallet toEntity(WalletCreateRequest request) {
        return Wallet.builder()
                .currency(request.currency())
                .balance(BigDecimal.ZERO)
                .build();
    }
}
