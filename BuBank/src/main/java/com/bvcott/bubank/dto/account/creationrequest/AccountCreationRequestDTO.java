package com.bvcott.bubank.dto.account.creationrequest;

import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AccountCreationRequestDTO {
    private Long requestId;
    private String accountType;
    private String status;
    private String requestedByUsername;
    private String reviewedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private List<AdminCommentDTO> adminComments;

    public AccountCreationRequestDTO(AccountCreationRequest request) {
        this.requestId = request.getRequestId();
        this.accountType = request.getAccountType();
        this.status = request.getStatus().toString();
        this.requestedByUsername = request.getRequestedBy() != null ? request.getRequestedBy().getUsername() : null;
        this.reviewedByUsername = request.getReviewedBy() != null ? request.getReviewedBy().getUsername() : null;
        this.createdAt = request.getCreatedAt();
        this.reviewedAt = request.getReviewedAt();
        this.adminComments = request.getAdminComments().stream()
                .map(comment -> new AdminCommentDTO(
                        comment.getAdmin().getUsername(),
                        comment.getComment(),
                        comment.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
