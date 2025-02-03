package com.bvcott.bubank.repository.account.creationrequest;

import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCreationRequestRepository extends JpaRepository<AccountCreationRequest, Long> {
}
