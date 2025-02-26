package com.bvcott.bubank.configuration.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GlobalSeeder implements CommandLineRunner {
    private UserSeeder userSeeder;
    private AccountSeeder accountSeeder;
    private TransactionSeeder txnSeeder;

    public GlobalSeeder(UserSeeder userSeeder, AccountSeeder accountSeeder, TransactionSeeder txnSeeder) {
        this.userSeeder = userSeeder;
        this.accountSeeder = accountSeeder;
        this.txnSeeder = txnSeeder;
    }

    @Override public void run(String... args) throws Exception{
        userSeeder.seed();
        accountSeeder.seed();
        txnSeeder.seed();
    }
    
}  
