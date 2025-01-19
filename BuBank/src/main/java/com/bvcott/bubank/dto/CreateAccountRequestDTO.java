package com.bvcott.bubank.dto;

import lombok.Data;

@Data
public class CreateAccountRequestDTO {
    private Long userId;
    private String accountType;
}
