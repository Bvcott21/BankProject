package com.bvcott.bubank.dto.account.creationrequest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class AdminCommentDTO {
    private String username;
    private String comment;
    private LocalDateTime timestamp;
}
