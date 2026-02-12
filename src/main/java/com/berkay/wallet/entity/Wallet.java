package com.berkay.wallet.entity;

import com.berkay.wallet.entity.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "currency"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private long version;
}
