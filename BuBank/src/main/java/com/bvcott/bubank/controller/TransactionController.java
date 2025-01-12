package com.bvcott.bubank.controller;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransferTransaction;
import com.bvcott.bubank.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController @RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService txnService;
    private final static Logger log = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(TransactionService txnService) {
        this.txnService = txnService;
    }

    @PreAuthorize("@accountService.isOwner(#transactionDTO.accountNumber, authentication.name)")
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionDTO dto) {
        log.info("Creating transaction: {}", dto);

        Transaction txn = txnService.createTransaction(dto);

        return ResponseEntity.ok(txn);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsForAccount(@PathVariable String accountNumber) {
        List<Transaction> txns = txnService.getTransactionsForAccount(accountNumber);

        return ResponseEntity.ok(txns);
    }
}
