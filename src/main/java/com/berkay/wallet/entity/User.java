package com.berkay.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Wallet> wallets = new HashSet<>();

    public Set<Wallet> getWallets() {
        return Collections.unmodifiableSet(wallets);
    }

    public void addWallets(Wallet wallet) {
        wallets.add(wallet);
        wallet.setUser(this);
    }

    public void removeWallets(String walletId) {
        Iterator<Wallet> iterator = wallets.iterator();

        while (iterator.hasNext()) {
            Wallet w = iterator.next();
            if (w.getId().toString().equals(walletId)) {
                iterator.remove();
                w.setUser(null);
                return;
            }
        }
    }
}
