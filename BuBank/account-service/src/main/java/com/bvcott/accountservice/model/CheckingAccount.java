package com.bvcott.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity @Data @NoArgsConstructor @Table(name = "CHECKING_ACCOUNTS")
public class CheckingAccount extends Account {
    @Column(nullable = false)
    private BigDecimal overdraftLimit;
}