package com.berkay.wallet.service;


import com.berkay.wallet.dto.TransactionRequest;
import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import com.berkay.wallet.repository.UserRepository;
import com.berkay.wallet.repository.WalletRepository;
import com.berkay.wallet.sevice.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID walletId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test@email.com");
        user.setPasswordHash("secret-pass");
        this.userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(user)
                .currency(Currency.TRY)
                .balance(BigDecimal.ZERO)
                .build();
        this.walletRepository.save(wallet);
        user.addWallets(wallet);
        this.walletId = wallet.getId();
    }

    @Test
    void shouldHandleConcurrentDeposits() throws Exception {
        int numberOfThread = 10;
        int amountOfThread = 10;

        CountDownLatch downLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThread; i++) {
            executorService.submit(() -> {
                try {
                    downLatch.await();
                    TransactionRequest request = new TransactionRequest(walletId, BigDecimal.valueOf(amountOfThread));
                    this.transactionService.deposit(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Exception:  " + e.getMessage());
                    failedCount.incrementAndGet();
                }
            });
        }
        downLatch.countDown();

        Thread.sleep(2000);

        Wallet updateWallet = this.walletRepository.findById(walletId).orElseThrow();
        System.out.println("Success count: " + successCount.get());
        System.out.println("Fail count: " + failedCount.get());
        System.out.println("Balance: " + updateWallet.getBalance());
        System.out.println("Wallet version: " + updateWallet.getVersion());

    }
}
