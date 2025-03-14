package com.bvcott.accountservice.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CustomerDTO {
    private Long userId;
    private String username;
    private String role;
    private List<AccountDTO> accounts;
}
