package com.bvcott.bubank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
