package com.bvcott.bubank.controller.account.creationrequest;

import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.service.account.creationrequest.AccountCreationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/v1/account-requests")
public class AccountCreationRequestController {
    private final AccountCreationRequestService requestService;

    public AccountCreationRequestController(AccountCreationRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AccountCreationRequestDTO>> getAllAccountRequests() {
        List<AccountCreationRequest> requests = requestService.getAllRequests();
        List<AccountCreationRequestDTO> dtos = requests.stream()
                .map(AccountCreationRequestDTO::new)
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }
}
