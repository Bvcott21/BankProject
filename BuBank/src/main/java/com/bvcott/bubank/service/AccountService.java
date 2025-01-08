package com.bvcott.bubank.service;

import com.bvcott.bubank.model.account.BusinessAccount;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
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

        String nextAccountNumber = generateNextAccountNumber(dto.getAccountType());
            
        Account account;
        switch(dto.getAccountType().toLowerCase()) {
            case "checking":
                CheckingAccount checkingAccount = new CheckingAccount();
                checkingAccount.setOverdraftLimit(dto.getOverdraftLimit());
                account = checkingAccount;
                break;
            case "savings":
                SavingsAccount savingsAccount = new SavingsAccount();
                savingsAccount.setInterestRate(dto.getInterestRate());
                account = savingsAccount;
                break;
            case "business":
                BusinessAccount businessAccount = new BusinessAccount();
                businessAccount.setCreditLimit(dto.getCreditLimit());
                account = businessAccount;
                break;
            default:
                throw new IllegalArgumentException("Invalid account type specified.");
        }

        account.setAccountNumber(nextAccountNumber);
        account.setBalance(dto.getInitialBalance());
        user.addAccount(account);

        userRepo.save(user);

        return account;
    }

    private String generateNextAccountNumber(String accountType) {
        String prefix = switch (accountType.toLowerCase()) {
            case "checking" -> "ACC-CHK-";
            case "savings" -> "ACC-SAV-";
            case "business" -> "ACC-BUS-";
            default ->
                    throw new IllegalArgumentException("Invalid account type specified for account number generation.");
        };

        String lastAccountNumber = accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc(prefix)
                .map(Account::getAccountNumber)
                .orElse(null);

        int nextNumber = 1;
        if(lastAccountNumber != null) {
            String[] parts = lastAccountNumber.split("-");
            nextNumber = Integer.parseInt(parts[2]) + 1;
        }

        return prefix + String.format("%08d", nextNumber);
    }
    
}
