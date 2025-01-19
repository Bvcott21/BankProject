package com.bvcott.bubank.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bvcott.bubank.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
