package com.bvcott.bubank.configuration.seeder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.service.AccountService;
import com.bvcott.bubank.service.TransactionService;

@Component
public class TransactionSeeder {
    private static final Logger log = LoggerFactory.getLogger(TransactionSeeder.class);
    private AccountRepository accountRepo;
    private AccountService accountService;
    private TransactionService txnService;

    public TransactionSeeder(AccountRepository accountRepo, AccountService accountService, TransactionService txnService) {
        this.accountRepo = accountRepo;
        this.accountService = accountService;
        this.txnService = txnService;
    }

    public void seed() {
        createDepositsAndWithdraws();
        createTransfers();
    }

    private void createDepositsAndWithdraws() {
        log.debug("[SEED] - Creating deposit and withdraws");

        List<Account> allAccounts = fetchAllAccounts();

        for(Account account : allAccounts) {
            long numberOfDeposits = Math.round(Math.random() * 25);
            BigDecimal amount = BigDecimal.valueOf(Math.random() * 25000);
            String deposit = "deposit", withdraw = "withdraw";
            
            Random random = new Random();
            String depositOrWithdraw = random.nextBoolean() ? deposit : withdraw;
            
            for(long i = 0; i < numberOfDeposits; i++) {
                switch(depositOrWithdraw) {
                    case "deposit":
                        log.debug("[SEED] - Creating deposit for account {} - with amount: {}", account.getAccountNumber(), amount);
                        accountService.deposit(account.getAccountNumber(), amount);
                    case "withdraw":
                        log.debug("[SEED] - Creating withdrawal for account {} - with amount: {}", account.getAccountNumber(), amount);
                        if(account.getBalance().doubleValue() > amount.doubleValue()) {
                            accountService.withdraw(account.getAccountNumber(), amount);
                        }
                    default:
                        break;
                }
            }
        }
    }

    private void createTransfers() {
        log.debug("[SEED] - Creating transfers");
        List<Account> allAccounts = fetchAllAccounts();
        long numberOfTransfers = Math.round(Math.random() * 25);

        for(Account account : allAccounts) {
            BigDecimal amount = BigDecimal.valueOf(Math.random() * 500);

            for(long i = 0; i < numberOfTransfers; i++) {
                int receiverAccountIndex = Math.toIntExact(Math.round(Math.random() * (allAccounts.size() - 1)));
                Account receiverAccount = allAccounts.get(receiverAccountIndex);

                if(account.getBalance().doubleValue() > amount.doubleValue()) {
                    TransactionDTO dto = TransactionDTO
                    .builder()
                    .accountNumber(account.getAccountNumber())
                    .receivingAccountNumber(receiverAccount.getAccountNumber())
                    .amount(amount.doubleValue())
                    .transactionType(TransactionType.TRANSFER)
                    .build();

                    txnService.createTransaction(dto);
                    log.debug("Creating transaction with details: {}", dto);
                } 

            }
        }
        
        


    }

    private List<Account> fetchAllAccounts() {
        return accountRepo.findAll();
    }
}
