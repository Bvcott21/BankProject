package com.bvcott.bubank.dto;

import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.model.transaction.merchant.MerchantCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    @NotBlank(message = "Account Number cannot be null or blank")
    private String accountNumber;

    private String receivingAccountNumber;

    @NotNull(message = "Transaction Type cannot be null.")
    private double amount;

    @NotNull(message = "Amount cannot be null")
    private TransactionType transactionType;

    private String merchantName;
    private MerchantCategory merchantCategory;
}
