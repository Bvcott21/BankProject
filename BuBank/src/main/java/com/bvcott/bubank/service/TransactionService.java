package com.bvcott.bubank.service;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.mapper.transaction.TransactionMapper;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.model.transaction.transfer.TransferDirection;
import com.bvcott.bubank.model.transaction.transfer.TransferTransaction;
import com.bvcott.bubank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {
    private final TransactionRepository txnRepo;
    private final AccountService accountService;
    private final TransactionMapper txnMapper;
    private final static Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository txnRepo, AccountService accountService, TransactionMapper txnMapper) {
        this.txnRepo = txnRepo;
        this.accountService = accountService;
        this.txnMapper = txnMapper;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO dto) {
        validateTransactionFields(dto);

        switch (dto.getTransactionType()) {
            case DEPOSIT:
                return processDeposit(dto);
            case WITHDRAWAL:
                return processWithdrawal(dto);
            case TRANSFER:
                return processTransfer(dto);
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + dto.getTransactionType());
        }
    }

    private void validateTransactionFields(TransactionDTO dto) {
        if (dto.getTransactionType() == TransactionType.TRANSFER) {
            if (dto.getReceivingAccountNumber() == null) {
                throw new IllegalArgumentException("Receiving account Number is required for transfer transactions.");
            }
        }
    }

    private Transaction processDeposit(TransactionDTO dto) {
        accountService.deposit(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));
        Transaction txn = createTransactionEntity(dto, generateNextTransactionNumber());
        return txnRepo.save(txn);
    }

    private Transaction processWithdrawal(TransactionDTO dto) {
        accountService.withdraw(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));
        Transaction txn = createTransactionEntity(dto, generateNextTransactionNumber());
        return txnRepo.save(txn);
    }

    private TransferTransaction processTransfer(TransactionDTO dto) {
        log.info("Processing transfer, dto: {}", dto);

        String txnNumber = generateNextTransactionNumber();

        // Withdraw from sender's account
        accountService.withdraw(dto.getAccountNumber(), BigDecimal.valueOf(dto.getAmount()));

        // Deposit into receiver's account
        accountService.deposit(dto.getReceivingAccountNumber(), BigDecimal.valueOf(dto.getAmount()));

        // Create and save sender transaction
        TransferTransaction senderTxn = createTransferTransactionEntity(dto, txnNumber, TransactionType.TRANSFER);
        senderTxn = txnRepo.save(senderTxn);
        log.info("Sender Transaction Created: {}", senderTxn);

        // Create receiver transaction linked to sender
        TransferTransaction receiverTxn = createReceiverTransactionEntity(dto, txnNumber, senderTxn.getTransactionId());
        log.info("Receiver Transaction Created: {}", receiverTxn);

        // Link sender to receiver
        senderTxn.setLinkedTransactionId(receiverTxn.getTransactionId());
        senderTxn = txnRepo.save(senderTxn); // Update sender
        log.info("Updated Sender Transaction with Linked ID: {}", senderTxn);

        return senderTxn;
    }

    private TransferTransaction createReceiverTransactionEntity(TransactionDTO dto, String transactionNumber, Long senderTransactionId) {
        TransferTransaction txn = new TransferTransaction();
        txn.setAccountNumber(dto.getReceivingAccountNumber()); // Receiver's account
        txn.setTransactionNumber(transactionNumber); // Shared transaction number
        txn.setTransactionType(TransactionType.TRANSFER); // Transaction type remains TRANSFER
        txn.setAmount(BigDecimal.valueOf(dto.getAmount())); // Set the transfer amount
        txn.setTimestamp(LocalDateTime.now()); // Current timestamp
        txn.setTransferDirection(TransferDirection.RECEIVER); // Explicitly set as RECEIVER
        txn.setLinkedTransactionId(senderTransactionId); // Link back to the sender's transaction
        txn.setSenderAccountNumber(dto.getAccountNumber()); // Set the sender's account correctly
        txn.setReceivingAccountNumber(dto.getReceivingAccountNumber()); // Set the receiver's account correctly

        log.info("Creating Receiver Transaction for Transfer: AccountNumber={}, ReceivingAccountNumber={}, SenderAccountNumber={}",
                dto.getReceivingAccountNumber(), dto.getReceivingAccountNumber(), dto.getAccountNumber());

        log.info("Receiver Transaction Finalized: {}", txn);
        return txnRepo.save(txn);
    }

    private Transaction createTransactionEntity(TransactionDTO dto, String transactionNumber) {
        // Convert most fields with mapper
        Transaction txn = txnMapper.toTransactionEntity(dto);

        // Manually set the fields you want to override
        txn.setTransactionNumber(transactionNumber);
        txn.setTimestamp(LocalDateTime.now());

        return txn;
    }

    private TransferTransaction createTransferTransactionEntity(TransactionDTO dto, String transactionNumber, TransactionType type) {
        // Let the mapper handle the basic copying of fields
        TransferTransaction txn = txnMapper.toTransferTransactionEntity(dto);

        // Add the custom logic specific to a transfer
        txn.setAccountNumber(dto.getAccountNumber());
        txn.setTransactionNumber(transactionNumber);
        txn.setTransactionType(type);
        txn.setTimestamp(LocalDateTime.now());
        txn.setTransferDirection(TransferDirection.SENDER);
        txn.setSenderAccountNumber(dto.getAccountNumber());
        // ...and so on

        return txn;
    }

    private String generateNextTransactionNumber() {
        String prefix = "TXN-";
        Optional<String> lastTransactionNumber = txnRepo.findTopByOrderByTransactionIdDesc()
                .map(Transaction::getTransactionNumber);

        int nextNumber = 1;
        if (lastTransactionNumber.isPresent()) {
            String[] parts = lastTransactionNumber.get().split("-");
            nextNumber = Integer.parseInt(parts[1]) + 1;
        }

        return prefix + String.format("%09d", nextNumber);
    }

    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        // Fetch sender transactions
        log.info("Fetching transactions where account {} is the sender...", accountNumber);
        List<TransferTransaction> senderTransactions = txnRepo.findByAccountAndDirection(accountNumber, TransferDirection.SENDER);

        // Fetch receiver transactions and populate senderAccountNumber
        log.info("Fetching transactions where account {} is the receiver...", accountNumber);
        List<TransferTransaction> receiverTransactions = txnRepo.findByAccountAndDirection(accountNumber, TransferDirection.RECEIVER)
                .stream()
                .peek(transferTxn -> {
                    if (transferTxn.getLinkedTransactionId() != null) {
                        txnRepo.findById(transferTxn.getLinkedTransactionId())
                                .ifPresent(linkedTxn -> transferTxn.setSenderAccountNumber(linkedTxn.getAccountNumber()));
                    }
                })
                .collect(Collectors.toList());

        // Combine sender and receiver transactions
        log.info("Combining sender and receiver transactions...");
        List<Transaction> allTransactions = Stream.concat(senderTransactions.stream(), receiverTransactions.stream())
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed()) // Sort by timestamp (desc)
                .collect(Collectors.toList());

        log.info("Returning {} transactions for account {}", allTransactions.size(), accountNumber);
        return allTransactions;
    }
}