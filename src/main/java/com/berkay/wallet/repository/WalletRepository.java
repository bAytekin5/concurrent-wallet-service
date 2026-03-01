package com.berkay.wallet.repository;

import com.berkay.wallet.entity.User;
import com.berkay.wallet.entity.Wallet;
import com.berkay.wallet.entity.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user WHERE w.user.id = :userId")
    List<Wallet> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user WHERE w.user.id = :userId AND w.currency = :currency")
    Optional<Wallet> findByUserIdAndCurrency(@Param("userId") UUID userId, @Param("currency") Currency currency);

    boolean existsByUserIdAndCurrency(UUID userId, Currency currency);
}
