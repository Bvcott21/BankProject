package com.bvcott.bubank.configuration.seeder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bvcott.bubank.dto.TransactionDTO;
import com.bvcott.bubank.dto.TransactionDTO.TransactionDTOBuilder;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.transaction.TransactionType;
import com.bvcott.bubank.model.transaction.merchant.MerchantCategory;
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
        createMerchantTransactions();
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
                        account = getAccountById(account.getId());
                        if(account.getBalance().doubleValue() > amount.doubleValue()) {
                            accountService.withdraw(account.getAccountNumber(), amount);
                        }
                    default:
                        break;
                }
            }
        }
    }

    private Account getAccountById(Long id) {
        return accountRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Can't find user"));
    }

    private void createTransfers() {
        log.debug("[SEED] - Creating transfers");
        List<Account> allAccounts = fetchAllAccounts();

        for(Account account : allAccounts) {
            long numberOfTransfers = Math.round(Math.random() * 25);
            

            for(long i = 0; i < numberOfTransfers; i++) {
                BigDecimal amount = BigDecimal.valueOf(Math.random() * 500);
                int receiverAccountIndex = Math.toIntExact(Math.round(Math.random() * (allAccounts.size() - 1)));
                Account receiverAccount = allAccounts.get(receiverAccountIndex);
                account = getAccountById(account.getId());

                if(account.getBalance().doubleValue() > amount.doubleValue() && account != receiverAccount) {
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

    private void createMerchantTransactions() {
        log.debug("[SEED] - Creating merchant transactions");
        List<Account> allAccounts = fetchAllAccounts();

        for(Account account : allAccounts) {
            long numberOfTransfers = Math.round(Math.random() * 65);
            

            for(long i = 0; i < numberOfTransfers; i++) {
                createMerchantTransaction(account);
            }
        }
    }

    private void createMerchantTransaction(Account account) {
        int transactionCategorySelector = Math
            .toIntExact(Math
                .round(Math.random() * (MerchantCategory.values().length - 1)));
        BigDecimal amount = BigDecimal.valueOf(Math.random() * 350);
        
        TransactionDTOBuilder txnBuilder = TransactionDTO
            .builder()
            .accountNumber(account.getAccountNumber())
            .transactionType(TransactionType.MERCHANT)
            .amount(amount.doubleValue());

        TransactionDTO dto = null;

        switch(transactionCategorySelector) {
            case 0:
                List<String> retailMerchants = new ArrayList<>(Arrays.asList("B&Q", "Dunkin' Donuts", "Amazon", "Apple"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.RETAIL)
                    .merchantName(merchantSelector(retailMerchants))
                    .build();
                break;
            case 1:
                List<String> groceriesMerchants = new ArrayList<>(Arrays.asList("Morrisons",  "Tesco", "Sainsbury's",  "Marks & Spencers"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.GROCERIES)
                    .merchantName(merchantSelector(groceriesMerchants))
                    .build();
                break;
            case 2:
                List<String> utilitiesMerchants = new ArrayList<>(Arrays.asList("British Gas", "OVO", "Good Energy", "Apple Store", "Microsoft"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.UTILITIES)
                    .merchantName(merchantSelector(utilitiesMerchants))
                    .build();
                break;
            case 3:
                List<String> transportMerchants = new ArrayList<>(Arrays.asList("TfL", "Trainline", "American Airlines", "British Airways"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.TRANSPORT)
                    .merchantName(merchantSelector(transportMerchants))
                    .build();
                break;
            case 4:
            List<String> entertainmentMerchants = new ArrayList<>(Arrays.asList("Vue", "Netflix", "Amazon Prime", "Apple TV+", "Crunchyroll"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.ENTERTAINMENT)
                    .merchantName(merchantSelector(entertainmentMerchants))
                    .build();
                break;
            case 5:
                List<String> otherMerchants = new ArrayList<>(Arrays.asList("The Corner Shop", "The Body Shop", "Parking Ticket"));
                dto = txnBuilder
                    .merchantCategory(MerchantCategory.OTHER)
                    .merchantName(merchantSelector(otherMerchants))
                    .build();
                break;
            default:
                break;
        }
        
        account = getAccountById(account.getId());
        if(account.getBalance().doubleValue() > amount.doubleValue()) {
            log.debug("Creating merchant transaction with details: {}", dto);
            txnService.createTransaction(dto);
        }
        
    }

    private String merchantSelector(List<String> merchants) {
        int randomIndex = new Random().nextInt(merchants.size());
        String randomMerchant = merchants.get(randomIndex);
        return randomMerchant;
    }

    private List<Account> fetchAllAccounts() {
        return accountRepo.findAll();
    }
}
