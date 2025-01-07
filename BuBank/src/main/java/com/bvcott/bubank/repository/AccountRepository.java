package com.bvcott.bubank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser_UserId(Long userId);
    Account findByAccountNumber(String accountNumber);
}
