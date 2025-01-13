package com.bvcott.bubank.service;

import com.bvcott.bubank.dto.TransactionDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {
    private final TransactionRepository txnRepo;
    private final AccountService accountService;
    private final static Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository txnRepo, AccountService accountService) {
        this.txnRepo = txnRepo;
        this.accountService = accountService;
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

        // Receiver's account perspective
        txn.setAccountNumber(dto.getReceivingAccountNumber()); // The receiver's account number
        txn.setTransactionNumber(transactionNumber); // Shared transaction number between sender and receiver
        txn.setTransactionType(TransactionType.TRANSFER); // Transaction type remains TRANSFER
        txn.setAmount(BigDecimal.valueOf(dto.getAmount())); // Transfer amount
        txn.setTimestamp(LocalDateTime.now()); // Current timestamp
        txn.setTransferDirection(TransferDirection.RECEIVER); // Indicate this is a receiver transaction

        // Link to sender's transaction
        txn.setLinkedTransactionId(senderTransactionId); // Linked to sender transaction by ID

        // Optional: Include sender's account number for display purposes
        txn.setSenderAccountNumber(dto.getAccountNumber()); // Sender's account number explicitly set

        return txnRepo.save(txn); // Save receiver transaction
    }

    private Transaction createTransactionEntity(TransactionDTO dto, String transactionNumber) {
        Transaction txn = new Transaction();
        txn.setAccountNumber(dto.getAccountNumber());
        txn.setTransactionNumber(transactionNumber);
        txn.setTransactionType(dto.getTransactionType());
        txn.setAmount(BigDecimal.valueOf(dto.getAmount()));
        txn.setTimestamp(LocalDateTime.now());
        return txn;
    }

    private TransferTransaction createTransferTransactionEntity(TransactionDTO dto, String transactionNumber, TransactionType type) {
        TransferTransaction txn = new TransferTransaction();

        // Sender's account perspective
        txn.setAccountNumber(dto.getAccountNumber()); // The sender's account number
        txn.setTransactionNumber(transactionNumber); // Shared transaction number between sender and receiver
        txn.setReceivingAccountNumber(dto.getReceivingAccountNumber()); // Receiver's account number
        txn.setTransactionType(type); // Transaction type remains TRANSFER
        txn.setAmount(BigDecimal.valueOf(dto.getAmount())); // Transfer amount
        txn.setTimestamp(LocalDateTime.now()); // Current timestamp
        txn.setTransferDirection(TransferDirection.SENDER); // Indicate this is a sender transaction

        // Optional: Include sender's account number explicitly
        txn.setSenderAccountNumber(dto.getAccountNumber()); // Sender's account number explicitly set

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
        List<Transaction> senderTransactions = txnRepo.findByAccountNumber(accountNumber)
                .stream()
                .filter(txn -> {
                    if (txn instanceof TransferTransaction transferTxn) {
                        return transferTxn.getTransferDirection() == TransferDirection.SENDER &&
                                transferTxn.getLinkedTransactionId() != null;
                    }
                    return true; // Include non-transfer transactions
                })
                .collect(Collectors.toList());

        // Fetch receiver transactions
        List<TransferTransaction> receiverTransactions = txnRepo.findByReceivingAccountNumber(accountNumber)
                .stream()
                .peek(transferTxn -> {
                    // Fetch sender transaction using linkedTransactionId
                    Optional<Transaction> linkedSenderTxn = txnRepo.findById(transferTxn.getLinkedTransactionId());
                    if (linkedSenderTxn.isPresent()) {
                        Transaction senderTxn = linkedSenderTxn.get();
                        transferTxn.setSenderAccountNumber(senderTxn.getAccountNumber());
                        log.info("Linked Sender Transaction Found: {}", senderTxn);
                    } else {
                        log.warn("No Sender Transaction Found for Linked ID: {}", transferTxn.getLinkedTransactionId());
                    }
                })
                .collect(Collectors.toList());

        // Combine transactions
        List<Transaction> allTransactions = Stream.concat(senderTransactions.stream(), receiverTransactions.stream())
                .sorted((txn1, txn2) -> txn2.getTimestamp().compareTo(txn1.getTimestamp())) // Sort by timestamp
                .collect(Collectors.toList());

        log.info("Filtered Transactions for account {}: {}", accountNumber, allTransactions);
        return allTransactions;
    }
}