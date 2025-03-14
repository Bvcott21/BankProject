package com.bvcott.accountservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                log.warn("No authenticated user found in security context");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }
            String username = auth.getName();
            log.info("getUserAccounts triggered for username: {}", username);
            return ResponseEntity.ok(accountService.listAccounts());
        } catch (Exception ex) {
            log.error("Error retrieving accounts: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving accounts");
        }
    }
}
