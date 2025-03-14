package com.bvcott.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "SAVINGS_ACCOUNT")
public class SavingsAccount extends Account {
    @Column(nullable = false)
    private BigDecimal interestRate;
}
