package com.bvcott.bubank.service.account.creationrequest;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.AdminComment;
import com.bvcott.bubank.model.account.creationrequest.RequestStatus;
import com.bvcott.bubank.model.user.Admin;
import com.bvcott.bubank.repository.account.creationrequest.AccountCreationRequestRepository;
import com.bvcott.bubank.repository.user.AdminRepository;
import com.bvcott.bubank.repository.user.CustomerRepository;
import com.bvcott.bubank.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountCreationRequestService {
    private final AccountCreationRequestRepository requestRepo;
    private final AdminRepository adminRepo;
    private final CustomerRepository customerRepo;
    private final AccountService accountService;

    public AccountCreationRequestService(
            AccountCreationRequestRepository requestRepo,
            AdminRepository adminRepo,
            CustomerRepository customerRepo,
            AccountService accountService) {
        this.requestRepo = requestRepo;
        this.adminRepo = adminRepo;
        this.customerRepo = customerRepo;
        this.accountService = accountService;
    }

    public List<AccountCreationRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    public AccountCreationRequestDTO addCommentToCreationRequest(Long requestId, AdminCommentDTO commentDTO, String username) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

        AccountCreationRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Account Creation Request not found for id: "+ requestId));

        if(!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("Can't comment further on an already closed request.");
        }

        AdminComment comment = new AdminComment();
        comment.setRequest(request);
        comment.setComment(commentDTO.getComment());
        comment.setAdmin(admin);
        comment.setTimestamp(LocalDateTime.now());

        request.addComment(comment);
        request = requestRepo.save(request);

        return new AccountCreationRequestDTO(request);
    }

    public AccountCreationRequestDTO getRequestById(Long requestId) {
        AccountCreationRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Account Creation Request not found for id: " + requestId));

        return new AccountCreationRequestDTO(request);
    }

    public AccountCreationRequestDTO updateAccountCreationRequestStatus(Long requestId, String username, RequestStatus newStatus, CreateAccountDTO dto) {
        AccountCreationRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Account creation request not found for id: " + requestId));

        Admin reviewedBy = adminRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

        if(!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new RuntimeException("A decision has already been taken on this request!");
        }

        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewedBy);
        request.setStatus(newStatus);

        request = requestRepo.save(request);

        if(newStatus.equals(RequestStatus.APPROVED)) {
            accountService.createAccount(dto, request.getRequestedBy().getUsername());
        }

        return new AccountCreationRequestDTO(request);
    }
}
