package com.bvcott.bubank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
