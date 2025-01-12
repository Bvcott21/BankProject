package com.bvcott.bubank.repository;

import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNumber(String accountNumber);
    @Query("SELECT t FROM TransferTransaction t WHERE t.receivingAccountNumber = :receivingAccountNumber")
    List<TransferTransaction> findByReceivingAccountNumber(@Param("receivingAccountNumber") String receivingAccountNumber);
}
