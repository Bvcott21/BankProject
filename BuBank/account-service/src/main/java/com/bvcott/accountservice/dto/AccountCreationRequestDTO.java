package com.bvcott.accountservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.bvcott.accountservice.model.AccountCreationRequest;

import lombok.Data;

@Data
public class AccountCreationRequestDTO {
    private Long requestId;
    private String accountType;
    private String status;
    private Long requestedBy;
    private Long reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private List<AdminCommentDTO> adminComments;

    public AccountCreationRequestDTO(AccountCreationRequest request) {
        this.requestId = request.getRequestId();
        this.accountType = request.getAccountType();
        this.status = request.getStatus().toString();
        this.requestedBy = request.getRequestedBy();
        this.reviewedBy = request.getReviewedBy();
        this.createdAt = request.getCreatedAt();
        this.reviewedAt = request.getReviewedAt();
        this.adminComments = request.getAdminComments().stream()
                .map(comment -> new AdminCommentDTO(
                        comment.getAdminId(),
                        comment.getComment(),
                        comment.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
