package com.berkay.wallet.sevice;

import com.berkay.wallet.dto.WalletCreateRequest;
import com.berkay.wallet.dto.WalletResponse;

public interface WalletService {

    WalletResponse createWallet(WalletCreateRequest request);
}
