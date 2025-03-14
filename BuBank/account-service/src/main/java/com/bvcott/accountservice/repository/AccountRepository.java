package com.bvcott.accountservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.accountservice.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findTopByAccountNumberStartingWithOrderByIdDesc(String prefix);
    boolean existsByAccountNumberAndCustomerId(String accountNumber, Long customerId);
}
