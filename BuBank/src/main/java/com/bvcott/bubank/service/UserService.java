package com.bvcott.bubank.service;

import com.bvcott.bubank.model.user.Customer;
import com.bvcott.bubank.model.user.Role;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.repository.user.UserRepository;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerCustomer(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(hashedPassword);
        customer.setRole(Role.ROLE_CUSTOMER);
        return userRepository.save(customer);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found with the provided username"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singletonList(() -> 
                user.getRole().name())
            )
            .build();
    }

    
}