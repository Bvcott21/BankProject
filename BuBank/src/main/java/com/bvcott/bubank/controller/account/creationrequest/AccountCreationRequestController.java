package com.bvcott.bubank.controller.account.creationrequest;

import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.service.account.creationrequest.AccountCreationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("/api/v1/account-requests")
public class AccountCreationRequestController {
    private final AccountCreationRequestService requestService;

    public AccountCreationRequestController(AccountCreationRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AccountCreationRequest>> getAllAccountRequests() {
        List<AccountCreationRequest> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }
}
