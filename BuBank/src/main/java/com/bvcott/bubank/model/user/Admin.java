package com.bvcott.bubank.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "ADMIN_USER") @Data
public class Admin extends User {
    public Admin() {
        super();
        this.setRole(Role.ROLE_ADMIN);
    }
}
