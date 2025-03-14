package com.bvcott.accountservice.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // Assume you have stored the token in the authentication details or credentials
            Object tokenObj = auth.getCredentials();
            if (tokenObj != null && tokenObj.toString().startsWith("Bearer ")) {
                template.header("Authorization", tokenObj.toString());
            } else {
                // Alternatively, if you stored the token elsewhere (for example, in details)
                Object details = auth.getDetails();
                if(details != null && details.toString().startsWith("Bearer ")) {
                    template.header("Authorization", details.toString());
                }
            }
        }
    }
}
