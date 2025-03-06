package com.bvcott.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "ADMIN_USER")
public class Admin extends User {

    public Admin(String username, String password, Role userRole) {
        super(username, password, userRole);
    }

    public Admin() {
        super();
        this.setRole(Role.ROLE_ADMIN);
    }
}
