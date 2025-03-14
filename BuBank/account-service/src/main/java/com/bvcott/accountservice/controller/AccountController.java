package com.bvcott.accountservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.accountservice.dto.AccountDTO;
import com.bvcott.accountservice.service.AccountService;

@RestController @RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // @PostMapping("/create-request")
    // public ResponseEntity<?> createAccountRequest(@RequestBody @Valid CreateAccountRequestDTO dto) {
    //     log.info("createAccount triggered with values: dto - {}, SecurityContext: {}", dto, SecurityContextHolder.getContext());
    //     try {
    //         // Retrieve logged-in user from SecurityContext
    //         String username = SecurityContextHolder
    //                 .getContext()
    //                 .getAuthentication()
    //                 .getName();
    //         log.info("username: {} ", username);
    //         AccountCreationRequest request = accountService.createAccountRequest(dto, username);
    //         return ResponseEntity.ok(request);
    //     } catch (RuntimeException ex) {
    //         // For now, return a simple error response
    //         return ResponseEntity.status(500).body(ex.getMessage());
    //     }
    // }

    @GetMapping
    public ResponseEntity<?> getUserAccounts() {
        log.info("getUserAccounts triggered");
        try {
            String username = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
            log.info("username: {}", username);
            List<AccountDTO> accounts = accountService.listAccounts(username);
            return ResponseEntity.ok(accounts);
        } catch(RuntimeException ex) {
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }
}
