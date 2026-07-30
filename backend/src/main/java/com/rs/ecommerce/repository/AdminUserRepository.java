package com.rs.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rs.ecommerce.model.AdminUser;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
}
