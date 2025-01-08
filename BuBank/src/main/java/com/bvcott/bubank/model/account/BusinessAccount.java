package com.bvcott.bubank.model.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity @Data @NoArgsConstructor @Table(name = "BUSINESS_ACCOUNTS")
public class BusinessAccount extends Account {
    @Column(nullable = false)
    private BigDecimal creditLimit;
}
