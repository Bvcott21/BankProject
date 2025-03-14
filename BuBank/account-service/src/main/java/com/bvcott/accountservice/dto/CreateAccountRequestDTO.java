package com.bvcott.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreateAccountRequestDTO {
    @NotBlank
    private String accountType;
}
