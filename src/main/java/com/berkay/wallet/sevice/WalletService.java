package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;
import com.berkay.wallet.entity.Wallet;

public interface WalletService {

    Wallet createWallet(WalletCreateRequest request);

    Wallet getWalletById(String id);
}
