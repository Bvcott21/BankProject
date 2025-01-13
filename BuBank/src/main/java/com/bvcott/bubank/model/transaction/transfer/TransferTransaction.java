package com.bvcott.bubank.model.transaction.transfer;

import com.bvcott.bubank.model.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Data;

@Entity @Table(name = "TRANSFER_TRANSACTIONS") @Data
public class TransferTransaction extends Transaction {
    private String receivingAccountNumber;

    @Column(name = "LINKED_TRANSACTION_ID")
    private Long linkedTransactionId;

    @Enumerated(EnumType.STRING)
    private TransferDirection transferDirection;

    @Transient
    private String senderAccountNumber;
}
