package com.bvcott.bubank.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data @Builder @Getter
public class CreateAccountDTO {
    private Long userId;
    private BigDecimal initialBalance;
    private String accountType;

    private BigDecimal overdraftLimit;
    private BigDecimal interestRate;
    private BigDecimal creditLimit;
}
