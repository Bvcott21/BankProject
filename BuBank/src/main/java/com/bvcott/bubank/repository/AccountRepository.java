package com.bvcott.bubank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.account.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser_UserId(Long userId);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    Account findTopByOrderByIdDesc();
    Optional<Account> findTopByAccountNumberStartingWithOrderByIdDesc(String prefix);
    boolean existsByAccountNumberAndUser_Username(String accountNumber, String username);
}
