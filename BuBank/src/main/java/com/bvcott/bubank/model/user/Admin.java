package com.bvcott.bubank.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "ADMIN_USER")
public class Admin extends User {
    public Admin() {
        super();
        this.setRole(Role.ROLE_ADMIN);
    }
}
