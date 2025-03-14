package com.bvcott.userservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bvcott.userservice.dto.AccountDTO;

@FeignClient(name = "account-service", url="${account.service.url}")
public interface AccountClient {
    @GetMapping("/accounts/customer/{customerId}")
    List<AccountDTO> getAccountsByCustomerId(@PathVariable("customerId") Long customerId);
}
