package com.bvcott.bubank.service;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.model.transaction.TransferTransaction;
import com.bvcott.bubank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {
    private final TransactionRepository txnRepo;
    private final AccountService accountService;

    public TransactionService(TransactionRepository txnRepo, AccountService accountService) {
        this.txnRepo = txnRepo;
        this.accountService = accountService;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO dto) {
        validateTransactionFields(dto);

        switch(dto.getTransactionType()) {
            case DEPOSIT:
                return processDeposit(dto);
            case WITHDRAWAL:
                return processWithdrawal(dto);
            case TRANSFER:
                return processTransfer(dto);
            default:
                throw new IllegalArgumentException("Unsupporter transaction type." + dto.getTransactionType());
        }
    }

    private void validateTransactionFields(TransactionDTO dto) {
        if(dto.getTransactionType() == TransactionType.TRANSFER) {
            if(dto.getReceivingAccountNumber() == null) {
                throw new IllegalArgumentException("Receiving account ID is required for transfer transactions.");
            }
        }
    }

    private Transaction processDeposit(TransactionDTO dto) {
        accountService.deposit(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));
        Transaction txn = createTransactionEntity(dto);
        return txnRepo.save(txn);
    }

    private Transaction processWithdrawal(TransactionDTO dto) {
        accountService.withdraw(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));
        Transaction txn = createTransactionEntity(dto);
        return txnRepo.save(txn);
    }

    private TransferTransaction processTransfer(TransactionDTO dto) {
        // Withdraw from sender's account
        accountService.withdraw(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));

        // Deposit into receiver's account
        accountService.deposit(dto.getReceivingAccountNumber(), BigDecimal.valueOf(dto.getAmount()));

        // Create Transaction for sender
        TransferTransaction senderTxn = createTransferTransactionEntity(dto);
        senderTxn = txnRepo.save(senderTxn);

        createReceiverTransactionEntity(dto);
        return senderTxn;
    }

    private void createReceiverTransactionEntity(TransactionDTO dto) {
        TransferTransaction txn = new TransferTransaction();
        txn.setAccountNumber(dto.getReceivingAccountNumber());
        txn.setTransactionType(TransactionType.TRANSFER);
        txn.setAmount(BigDecimal.valueOf(dto.getAmount()));
        txn.setReceivingAccountNumber(dto.getAccountNumber()); // Setting the sender's account number
        txn.setTimestamp(LocalDateTime.now());
        txnRepo.save(txn);
    }

    private Transaction createTransactionEntity(TransactionDTO dto) {
        Transaction txn = new Transaction();
        txn.setAccountNumber(dto.getAccountNumber());
        txn.setTransactionType(dto.getTransactionType());
        txn.setAmount(BigDecimal.valueOf(dto.getAmount()));
        txn.setTimestamp(LocalDateTime.now());
        return txn;
    }

    private TransferTransaction createTransferTransactionEntity(TransactionDTO dto) {
        TransferTransaction txn = new TransferTransaction();
        txn.setAccountNumber(dto.getAccountNumber());
        txn.setReceivingAccountNumber(dto.getReceivingAccountNumber());
        txn.setTransactionType(dto.getTransactionType());
        txn.setAmount(BigDecimal.valueOf(dto.getAmount()));
        txn.setTimestamp(LocalDateTime.now());
        return txn;
    }

    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        // Fetch transactions where the account is the sender (TRANSFER-OUT and others)
        List<Transaction> sentTransactions = txnRepo.findByAccountNumber(accountNumber)
                .stream()
                .filter(txn -> !(txn instanceof TransferTransaction) ||
                        ((TransferTransaction) txn).getAccountNumber().equals(accountNumber)) // Sender transactions only
                .toList();

        // Fetch transactions where the account is the receiver (TRANSFER-IN only)
        List<TransferTransaction> receivedTransactions = txnRepo.findByReceivingAccountNumber(accountNumber)
                .stream()
                .filter(txn -> txn.getReceivingAccountNumber().equals(accountNumber)) // Receiver transactions only
                .toList();

        // Combine both lists
        return Stream.concat(sentTransactions.stream(), receivedTransactions.stream())
                .collect(Collectors.toList());
    }
}
