package com.bvcott.accountservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.bvcott.accountservice.dto.CustomerDTO;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    CustomerDTO getCustomerById(Long id);

    // Need to double check and fix this one
    @GetMapping("/api/v1/customers")
    CustomerDTO getCustomerByUsername();
}
