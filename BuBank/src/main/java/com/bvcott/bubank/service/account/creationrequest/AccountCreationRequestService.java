package com.bvcott.bubank.service.account.creationrequest;

import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.AdminComment;
import com.bvcott.bubank.model.user.Admin;
import com.bvcott.bubank.repository.account.creationrequest.AccountCreationRequestRepository;
import com.bvcott.bubank.repository.user.AdminRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountCreationRequestService {
    private final AccountCreationRequestRepository requestRepo;
    private final AdminRepository adminRepo;

    public AccountCreationRequestService(
            AccountCreationRequestRepository requestRepo,
            AdminRepository adminRepo) {
        this.requestRepo = requestRepo;
        this.adminRepo = adminRepo;
    }

    public List<AccountCreationRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    public AccountCreationRequestDTO addCommentToCreationRequest(Long requestId, AdminCommentDTO commentDTO, String username) {
        Admin admin = adminRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

        AccountCreationRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Account Creation Request not found for id: "+ requestId));

        AdminComment comment = new AdminComment();
        comment.setRequest(request);
        comment.setComment(commentDTO.getComment());
        comment.setAdmin(admin);
        comment.setTimestamp(LocalDateTime.now());

        request.addComment(comment);
        request = requestRepo.save(request);

        return new AccountCreationRequestDTO(request);
        //TODO - Create frontend page to see request details, including list of comments.
    }
}
