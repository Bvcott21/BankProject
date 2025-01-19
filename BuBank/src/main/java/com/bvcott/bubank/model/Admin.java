package com.bvcott.bubank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity @Table(name = "ADMIN_USER") @NoArgsConstructor
public class Admin extends User {
    public Admin() {
        super();
        this.setRole(Role.ROLE_ADMIN);
    }
}
