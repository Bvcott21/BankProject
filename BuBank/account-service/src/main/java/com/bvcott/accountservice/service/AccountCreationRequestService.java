package com.bvcott.accountservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bvcott.accountservice.model.AccountCreationRequest;
import com.bvcott.accountservice.repository.AccountCreationRequestRepository;

@Service
public class AccountCreationRequestService {
    private final AccountCreationRequestRepository requestRepo;
    private final AccountService accountService;

    public AccountCreationRequestService(
            AccountCreationRequestRepository requestRepo,
            AccountService accountService) {
        this.requestRepo = requestRepo;
        this.accountService = accountService;
    }

    public List<AccountCreationRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    // @Transactional
    // public AccountCreationRequestDTO addCommentToCreationRequest(Long requestId, AdminCommentDTO commentDTO, String username) {
    //     Admin admin = adminRepo.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

    //     AccountCreationRequest request = requestRepo.findById(requestId)
    //             .orElseThrow(() -> new RuntimeException("Account Creation Request not found for id: "+ requestId));

    //     if(!request.getStatus().equals(RequestStatus.PENDING)) {
    //         throw new RuntimeException("Can't comment further on an already closed request.");
    //     }

    //     AdminComment comment = new AdminComment();
    //     comment.setRequest(request);
    //     comment.setComment(commentDTO.getComment());
    //     comment.setAdmin(admin);
    //     comment.setTimestamp(LocalDateTime.now());

    //     request.addComment(comment);
    //     request = requestRepo.save(request);

    //     return new AccountCreationRequestDTO(request);
    // }

    // public AccountCreationRequestDTO getRequestById(Long requestId) {
    //     AccountCreationRequest request = requestRepo.findById(requestId)
    //             .orElseThrow(() -> new RuntimeException("Account Creation Request not found for id: " + requestId));

    //     return new AccountCreationRequestDTO(request);
    // }

    // @Transactional
    // public AccountCreationRequestDTO updateAccountCreationRequestStatus(Long requestId, String username, RequestStatus newStatus, CreateAccountDTO dto) {
    //     AccountCreationRequest request = requestRepo.findById(requestId)
    //             .orElseThrow(() -> new RuntimeException("Account creation request not found for id: " + requestId));

    //     Admin reviewedBy = adminRepo.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

    //     if(!request.getStatus().equals(RequestStatus.PENDING)) {
    //         throw new RuntimeException("A decision has already been taken on this request!");
    //     }

    //     request.setReviewedAt(LocalDateTime.now());
    //     request.setReviewedBy(reviewedBy);
    //     request.setStatus(newStatus);

    //     request = requestRepo.save(request);

    //     if(newStatus.equals(RequestStatus.APPROVED)) {
    //         accountService.createAccount(dto, request.getRequestedBy().getUsername());
    //     }

    //     return new AccountCreationRequestDTO(request);
    // }
}
