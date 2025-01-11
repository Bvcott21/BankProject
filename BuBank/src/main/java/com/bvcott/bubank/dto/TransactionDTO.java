package com.bvcott.bubank.dto;

import com.bvcott.bubank.model.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @NotNull(message = "Account ID cannot be null.")
    private Long accountId;

    @NotNull(message = "Transaction Type cannot be null.")
    private double amount;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private TransactionType transactionType;
}
