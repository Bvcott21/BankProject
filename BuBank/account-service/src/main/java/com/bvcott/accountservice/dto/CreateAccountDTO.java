package com.bvcott.accountservice.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreateAccountDTO {
    private BigDecimal initialBalance;
    private String accountType;

    private BigDecimal overdraftLimit;
    private BigDecimal interestRate;
    private BigDecimal creditLimit;
}
