package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import org.apache.catalina.LifecycleState;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    Wallet createWallet(WalletCreateRequest request);

    Wallet getWalletById(UUID id);

    Wallet getWalletByUserIdAndCurrency(UUID userId, Currency currency);

    List<Wallet> getWalletsByUserId(UUID userId);
}
