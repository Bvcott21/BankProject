package com.bvcott.bubank.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data @Builder @Getter
public class CreateAccountDTO {
    private Long userId;
    private BigDecimal initialBalance;
}
