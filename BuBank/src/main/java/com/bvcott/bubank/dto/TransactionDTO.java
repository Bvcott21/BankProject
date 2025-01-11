package com.bvcott.bubank.dto;

import com.bvcott.bubank.model.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @NotBlank(message = "Account Number cannot be null or blank")
    private String accountNumber;

    private String receivingAccountNumber;

    @NotNull(message = "Transaction Type cannot be null.")
    private double amount;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private TransactionType transactionType;
}
