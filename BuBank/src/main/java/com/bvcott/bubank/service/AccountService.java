package com.bvcott.bubank.service;

import com.bvcott.bubank.model.account.BusinessAccount;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    
    public AccountService(AccountRepository accountRepo, UserRepository userRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Account createAccount(CreateAccountDTO dto, String username) {
        log.info("Create account triggered with values: dto - {}, username: {}", dto, username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found."));

        log.info("User found, creating account...");
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

    public List<Account> listUserAccounts(String username) {
        log.info("Finding account for username: {}", username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found."));

        return user.getAccounts();
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

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = findAccountByAccountNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        accountRepo.save(account);
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAccountByAccountNumber(accountNumber);

        if (account instanceof BusinessAccount) {
            validateBusinessAccountWithdrawal((BusinessAccount) account, amount);
        } else if (account instanceof CheckingAccount) {
            validateCheckingAccountWithdrawal((CheckingAccount) account, amount);
        } else if (account instanceof SavingsAccount) {
            validateSavingsAccountWithdrawal((SavingsAccount) account, amount);
        } else {
            throw new IllegalArgumentException("Unsupported account type for withdrawal.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepo.save(account);
    }

    private void validateBusinessAccountWithdrawal(BusinessAccount account, BigDecimal amount) {
        if (account.getBalance().subtract(amount).compareTo(account.getCreditLimit().negate()) < 0) {
            throw new RuntimeException("Insufficient funds for Business account. Cannot exceed credit limit of: " +
                    account.getCreditLimit());
        }
    }

    private void validateCheckingAccountWithdrawal(CheckingAccount account, BigDecimal amount) {
        if(account.getBalance().subtract(amount).compareTo(account.getOverdraftLimit().negate()) < 0) {
            throw new RuntimeException("Insufficient funds for Checking account. Cannot exceed overdraft limit of: " +
                    account.getOverdraftLimit());
        }
    }

    private void validateSavingsAccountWithdrawal(SavingsAccount account, BigDecimal amount) {
        if(account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds for Savings account. Cannot have negative balance.");
        }
    }

    public Account findAccountById(Long accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account findAccountByAccountNumber(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with the provided account number: " + accountNumber));
    }

    public boolean isOwner(Long accountId, String username) {
        return accountRepo.existsByIdAndUser_Username(accountId, username);
    }

    public void validateAccountExists(String accountNumber) {
        if(!accountRepo.existsByAccountNumber(accountNumber)) {
            throw new RuntimeException("Account not found: " + accountNumber);
        }
    }
    
}
