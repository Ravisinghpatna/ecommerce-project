package com.example.ecommerce.controller;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.LoginResponse;
import com.example.ecommerce.model.AdminUser;
import com.example.ecommerce.repository.AdminUserRepository;
import com.example.ecommerce.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // POST /api/auth/login  { "username": "admin", "password": "admin123" }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException(
                        "Invalid username or password"));

        // matches() -> plain text password ko user ke stored BCrypt hash se compare karta hai
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new org.springframework.security.authentication.BadCredentialsException(
                    "Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), "ADMIN", user.getId());
        return new LoginResponse(token, user.getUsername(), "ADMIN", user.getId());
    }
}
