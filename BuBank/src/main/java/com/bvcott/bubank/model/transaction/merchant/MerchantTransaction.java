package com.bvcott.bubank.model.transaction.merchant;

import com.bvcott.bubank.model.transaction.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

@Entity @Table(name = "MERCHANT_TRANSACTIONS") @Data
public class MerchantTransaction extends Transaction {
    private String merchantName;

    @Enumerated(EnumType.STRING)
    private MerchantCategory merchantCategory;
}
