package com.bvcott.bubank.dto.account.creationrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdminCommentDTO {
    private String username;
    private String comment;
    private LocalDateTime timestamp;
}
