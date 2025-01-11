package com.bvcott.bubank.service;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository txnRepo;
    private final AccountService accountService;

    public TransactionService(TransactionRepository txnRepo, AccountService accountService) {
        this.txnRepo = txnRepo;
        this.accountService = accountService;
    }

    public Transaction createTransaction(TransactionDTO dto) {
        Transaction txn = new Transaction();
        txn.setAccountId(dto.getAccountId());
        txn.setTransactionType(dto.getTransactionType());
        txn.setAmount(BigDecimal.valueOf(dto.getAmount()));
        txn.setTimestamp(LocalDateTime.now());
        return txnRepo.save(txn);
    }
}
