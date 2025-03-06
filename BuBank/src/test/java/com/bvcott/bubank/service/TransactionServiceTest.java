package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.mapper.transaction.TransactionMapper;
import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.model.transaction.merchant.MerchantTransaction;
import com.bvcott.bubank.model.transaction.transfer.TransferDirection;
import com.bvcott.bubank.repository.TransactionRepository;

class TransactionServiceTest {

    @Mock
    private TransactionRepository txnRepo;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionMapper txnMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDepositTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionType(TransactionType.DEPOSIT);
        dto.setAccountNumber("12345");
        dto.setAmount(100.0);

        Transaction transaction = new Transaction();
        when(txnMapper.toTransactionEntity(any())).thenReturn(transaction);
        when(txnRepo.save(any())).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(dto);

        verify(accountService).deposit("12345", BigDecimal.valueOf(100.0));
        verify(txnRepo).save(transaction);
        assertNotNull(result);
    }

    @Test
    void testCreateWithdrawalTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionType(TransactionType.WITHDRAWAL);
        dto.setAccountNumber("12345");
        dto.setAmount(100.0);

        Transaction transaction = new Transaction();
        when(txnMapper.toTransactionEntity(any())).thenReturn(transaction);
        when(txnRepo.save(any())).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(dto);

        verify(accountService).withdraw("12345", BigDecimal.valueOf(100.0));
        verify(txnRepo).save(transaction);
        assertNotNull(result);
    }



    @Test
    void testCreateMerchantTransaction() {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionType(TransactionType.MERCHANT);
        dto.setAccountNumber("12345");
        dto.setAmount(100.0);
        dto.setMerchantName("Test Merchant");

        MerchantTransaction merchantTransaction = new MerchantTransaction();
        when(txnMapper.toMerchantTransactionEntity(any())).thenReturn(merchantTransaction);
        when(txnRepo.save(any())).thenReturn(merchantTransaction);

        MerchantTransaction result = (MerchantTransaction) transactionService.createTransaction(dto);

        verify(accountService).withdraw("12345", BigDecimal.valueOf(100.0));
        verify(txnRepo).save(merchantTransaction);
        assertNotNull(result);
    }

    @Test
    void testGetTransactionsForAccount() {
        String accountNumber = "12345";
        when(txnRepo.findByAccountAndDirection(accountNumber, TransferDirection.SENDER)).thenReturn(List.of());
        when(txnRepo.findByAccountAndDirection(accountNumber, TransferDirection.RECEIVER)).thenReturn(List.of());
        when(txnRepo.findByAccountNumber(accountNumber)).thenReturn(List.of());

        List<Transaction> transactions = transactionService.getTransactionsForAccount(accountNumber);

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

}
