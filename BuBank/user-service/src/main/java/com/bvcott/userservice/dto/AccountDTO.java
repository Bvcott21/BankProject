package com.bvcott.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String accountType;
    private Double balance;
}
