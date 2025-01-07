package com.bvcott.bubank.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreateAccountDTO {
    private Long userId;
    private BigDecimal initialBalance;
}
