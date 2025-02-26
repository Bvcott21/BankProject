package com.bvcott.bubank.configuration.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GlobalSeeder implements CommandLineRunner {
    private UserSeeder userSeeder;
    private AccountSeeder accountSeeder;

    public GlobalSeeder(UserSeeder userSeeder, AccountSeeder accountSeeder) {
        this.userSeeder = userSeeder;
        this.accountSeeder = accountSeeder;
    }

    @Override public void run(String... args) throws Exception{
        userSeeder.seed();   
        accountSeeder.seed();     
    }
    
}  
