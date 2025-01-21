package com.bvcott.bubank.configuration;

import com.bvcott.bubank.model.user.Admin;
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
public class AdminDataLoader {
    private static final Logger log = LoggerFactory.getLogger(AdminDataLoader.class);

    @Bean
    CommandLineRunner initAdminUsers(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            Admin admin1 = new Admin();
            admin1.setUsername("admin1");
            admin1.setPassword(passwordEncoder.encode("12345"));
            admin1.setRole(Role.ROLE_ADMIN);

            Admin admin2 = new Admin();
            admin2.setUsername("admin2");
            admin2.setPassword(passwordEncoder.encode("12345"));
            admin2.setRole(Role.ROLE_ADMIN);

            userRepo.saveAll(List.of(admin1, admin2));
            log.info("Preloaded database with Admins");
        };
    }
}
