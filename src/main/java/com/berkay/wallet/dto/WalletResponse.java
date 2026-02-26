package com.berkay.wallet.dto;

import com.berkay.wallet.entity.enums.Currency;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class WalletResponse extends RepresentationModel<WalletResponse> {
    private UUID id;
    private UUID userId;
    private Currency currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}