package com.bvcott.bubank.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LoginSignupDTO {
    private String username;
    private String password;
}
