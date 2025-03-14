package com.bvcott.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity @Data @NoArgsConstructor @Table(name = "BUSINESS_ACCOUNTS")
public class BusinessAccount extends Account {
    @Column(nullable = false)
    private BigDecimal creditLimit;
}
