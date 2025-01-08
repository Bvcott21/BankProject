package com.bvcott.bubank.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.service.AccountService;

@RestController @RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody @Valid CreateAccountDTO dto) {
        try {
            Account account = accountService.createAccount(dto);
            return ResponseEntity.ok(account);
        } catch (RuntimeException ex) {
            // For now, return a simple error response
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }
}
