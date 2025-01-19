package com.bvcott.bubank.repository.user;

import com.bvcott.bubank.model.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
