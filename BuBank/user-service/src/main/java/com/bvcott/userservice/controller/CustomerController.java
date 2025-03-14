package com.bvcott.userservice.controller;

import java.security.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.userservice.dto.CustomerDTO;
import com.bvcott.userservice.service.UserService;

@RestController @RequestMapping("/api/v1/customers")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            CustomerDTO customer = userService.findCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getCustomerByUsername() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("getCustomerByUsername triggered for username: {}", username);
            CustomerDTO customer = userService.findCustomerByUsername(username);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }
}
