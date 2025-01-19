package com.bvcott.bubank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequestDTO {
    @NotBlank
    private String accountType;
}
