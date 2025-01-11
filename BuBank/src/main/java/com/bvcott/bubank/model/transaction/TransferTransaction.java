package com.bvcott.bubank.model.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity @Table(name = "TRANSFER_TRANSACTIONS") @Data
public class TransferTransaction extends Transaction {
    @Column(nullable = false)
    private String receivingAccountNumber;
}
