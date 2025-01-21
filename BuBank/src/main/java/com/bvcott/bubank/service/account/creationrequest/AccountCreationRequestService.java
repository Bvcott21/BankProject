package com.bvcott.bubank.service.account.creationrequest;

import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.repository.account.creationrequest.AccountCreationRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountCreationRequestService {
    private final AccountCreationRequestRepository requestRepo;

    public AccountCreationRequestService(AccountCreationRequestRepository requestRepo) {
        this.requestRepo = requestRepo;
    }

    public List<AccountCreationRequest> getAllRequests() {
        return requestRepo.findAll();
    }
}
