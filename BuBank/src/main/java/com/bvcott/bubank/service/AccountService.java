package com.bvcott.bubank.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.Account;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.UserRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
    
    public AccountService(AccountRepository accountRepo, UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    public Account createAccount(CreateAccountDTO dto) {
        User user = userRepo.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found.")); 
            
        Account account = new Account(); 
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(dto.getInitialBalance());

        user.addAccount(account);

        userRepo.save(user);

        return account;
    }
    
}
