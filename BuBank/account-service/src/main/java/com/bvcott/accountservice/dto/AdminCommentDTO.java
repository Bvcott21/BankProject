package com.bvcott.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class AdminCommentDTO {
    private Long adminId;
    private String comment;
    private LocalDateTime timestamp;
}
