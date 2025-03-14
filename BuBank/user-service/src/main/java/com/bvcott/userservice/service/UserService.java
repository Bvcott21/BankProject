package com.bvcott.userservice.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bvcott.userservice.client.AccountClient;
import com.bvcott.userservice.dto.AccountDTO;
import com.bvcott.userservice.dto.CustomerDTO;
import com.bvcott.userservice.model.Customer;
import com.bvcott.userservice.model.Role;
import com.bvcott.userservice.model.User;
import com.bvcott.userservice.repository.AdminRepository;
import com.bvcott.userservice.repository.CustomerRepository;
import com.bvcott.userservice.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountClient accountClient;
    private final CustomerRepository customerRepo;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
        AccountClient accountClient, AdminRepository adminRepository,
        CustomerRepository customerRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountClient = accountClient;
        this.adminRepository = adminRepository;
        this.customerRepo = customerRepo;
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
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with the provided username"));
    }

    public CustomerDTO findCustomerByUsername(String username) {
        Customer customer = customerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with the provided username"));
        
        return CustomerDTO.builder()
            .userId(customer.getUserId())
            .username(customer.getUsername())
            .role(customer.getRole().name())
            .accounts(accountClient.getAccountsByCustomerId(customer.getUserId()))
            .build();
    }

    public CustomerDTO findCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with the provided id"));
        List<AccountDTO> accounts = accountClient.getAccountsByCustomerId(id);

        return CustomerDTO.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .role(user.getRole().name())
            .accounts(accounts)
            .build();
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