package com.bvcott.bubank.repository;

import com.bvcott.bubank.model.transaction.Transaction;
import com.bvcott.bubank.model.transaction.transfer.TransferDirection;
import com.bvcott.bubank.model.transaction.transfer.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNumber(String accountNumber);
    @Query("SELECT t FROM TransferTransaction t WHERE t.receivingAccountNumber = :receivingAccountNumber")
    List<TransferTransaction> findByReceivingAccountNumber(@Param("receivingAccountNumber") String receivingAccountNumber);
    Optional<Transaction> findTopByOrderByTransactionIdDesc();
    @Query("SELECT t FROM TransferTransaction t WHERE t.accountNumber = :accountNumber AND t.transferDirection = :transferDirection")
    List<TransferTransaction> findByAccountAndDirection(
            @Param("accountNumber") String accountNumber,
            @Param("transferDirection") TransferDirection transferDirection);
    @Query(name = "Transaction.findMerchantTransactionsByAccountNumber")
    List<Transaction> findMerchantTransactionsByAccountNumber(@Param("accountNumber") String accountNumber);
}
