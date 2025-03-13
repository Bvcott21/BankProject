package com.bvcott.userservice.seed;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bvcott.userservice.model.Admin;
import com.bvcott.userservice.model.Customer;
import com.bvcott.userservice.model.Role;
import com.bvcott.userservice.model.User;
import com.bvcott.userservice.repository.UserRepository;

@Component
public class Seeder implements CommandLineRunner{
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(Seeder.class);

    public Seeder(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Override public void run(String... args) throws Exception {
        seed();
    }


    public void seed() {
        log.debug("[SEED] - Seeding database with users.");
        List<User> generatedUsers = generateUsers();
        persistUsers(generatedUsers);
        log.debug("[SEED] - Users seeded successfully");
    }

    private List<User> generateUsers() {
        log.debug("[SEED] - Creating customers");
        User customer1 = new Customer(
            "customer1",
            passwordEncoder.encode("test"),
            Role.ROLE_CUSTOMER
        );
        User customer2 = new Customer(
            "customer2",
            passwordEncoder.encode("test"),
            Role.ROLE_CUSTOMER
        );
        User customer3 = new Customer(
            "customer3",
            passwordEncoder.encode("test"),
            Role.ROLE_CUSTOMER
        );
        User customer4 = new Customer(
            "customer4",
            passwordEncoder.encode("test"),
            Role.ROLE_CUSTOMER
        );

        log.debug("[SEED] - Creating Admins");
        User admin1 = new Admin(
            "admin1",
            passwordEncoder.encode("test"),
            Role.ROLE_ADMIN
        );

        User admin2 = new Admin(
            "admin2",
            passwordEncoder.encode("test"),
            Role.ROLE_ADMIN
        );

        User admin3 = new Admin(
            "admin3",
            passwordEncoder.encode("test"),
            Role.ROLE_ADMIN
        );

        User admin4 = new Admin(
            "admin4",
            passwordEncoder.encode("test"),
            Role.ROLE_ADMIN
        );
        
        log.debug("[SEED] - Returning list of created users");
        return Arrays.asList(customer1, customer2, customer3, customer4, admin1, admin2, admin3, admin4);
    }

    private void persistUsers(List<User> users) {
        log.debug("[SEED] - Persisting created users...");
        userRepo.saveAll(users);
        log.debug("[SEED] - Users persisted successfully");
    }

}
