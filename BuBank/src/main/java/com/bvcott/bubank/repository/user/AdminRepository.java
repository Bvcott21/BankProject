package com.bvcott.bubank.repository.user;

import com.bvcott.bubank.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
