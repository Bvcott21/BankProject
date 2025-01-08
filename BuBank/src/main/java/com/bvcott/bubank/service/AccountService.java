package com.bvcott.bubank.service;

import org.springframework.stereotype.Service;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
    
    public AccountService(AccountRepository accountRepo, UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Account createAccount(CreateAccountDTO dto) {
        User user = userRepo.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found.")); 

        String nextAccountNumber = generateNextAccountNumber();
            
        Account account = new Account(); 
        account.setAccountNumber(nextAccountNumber);
        account.setBalance(dto.getInitialBalance());

        user.addAccount(account);

        userRepo.save(user);

        return account;
    }

    private String generateNextAccountNumber() {
        Account lastAccount = accountRepo.findTopByOrderByIdDesc();

        if(lastAccount == null || lastAccount.getAccountNumber() == null) {
            return "ACC-00000001";
        }

        String lastAccountNumber = lastAccount.getAccountNumber();
        int lastNumber = Integer.parseInt(lastAccountNumber.split("-")[1]);

        int nextNumber = lastNumber + 1;

        return String.format("ACC-%08d", nextNumber);

    }
    
}
