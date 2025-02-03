package com.bvcott.bubank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.account.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findTopByAccountNumberStartingWithOrderByIdDesc(String prefix);
    boolean existsByAccountNumberAndCustomer_Username(String accountNumber, String username);
}
