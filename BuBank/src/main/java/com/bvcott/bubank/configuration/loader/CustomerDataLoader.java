package com.bvcott.bubank.configuration.loader;

import com.bvcott.bubank.model.user.Admin;
import com.bvcott.bubank.model.user.Customer;
import com.bvcott.bubank.model.user.Role;
import com.bvcott.bubank.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class CustomerDataLoader {
    private static final Logger log = LoggerFactory.getLogger(CustomerDataLoader.class);

    @Bean
    CommandLineRunner initCustomerUsers(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            Customer customer1 = new Customer();
            customer1.setUsername("customer1");
            customer1.setPassword(passwordEncoder.encode("test"));
            customer1.setRole(Role.ROLE_CUSTOMER);

            Customer customer2 = new Customer();
            customer2.setUsername("customer2");
            customer2.setPassword(passwordEncoder.encode("test"));
            customer2.setRole(Role.ROLE_CUSTOMER);

            userRepo.saveAll(List.of(customer1, customer2));
            log.info("Preloaded database with Customers");
        };
    }
}
