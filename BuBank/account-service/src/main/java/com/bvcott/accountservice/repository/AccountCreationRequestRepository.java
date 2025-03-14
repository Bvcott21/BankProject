package com.bvcott.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.accountservice.model.AccountCreationRequest;

public interface AccountCreationRequestRepository extends JpaRepository<AccountCreationRequest, Long> {
}
