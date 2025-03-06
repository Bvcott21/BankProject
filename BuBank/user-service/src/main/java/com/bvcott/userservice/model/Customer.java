package com.bvcott.userservice.model;

import java.util.ArrayList;
import java.util.List;

import com.bvcott.userservice.dto.AccountDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity @Table(name = "CUSTOMER_USER") @Data @EqualsAndHashCode(callSuper = true)
public class Customer extends User {
    private transient List<AccountDTO> accounts = new ArrayList<>();

    public Customer(String username, String password, Role role) {
        super(username, password, role);
    }
    
    public Customer() {}
    
}
