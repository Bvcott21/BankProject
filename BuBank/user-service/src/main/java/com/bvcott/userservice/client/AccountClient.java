package com.bvcott.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bvcott.userservice.dto.AccountDTO;

@FeignClient(name = "account-service")
public interface AccountClient {
    @GetMapping("/accounts/customer/{customerId}")
    AccountDTO getAccountsByCustomerId(@PathVariable("customerId") Long customerId);
}
