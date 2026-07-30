package com.rs.ecommerce.security;

/**
 * Ye class ek "pehchaan card" hai jo har authenticated request ke saath
 * chipka rehta hai (SecurityContext ke andar). Isse controllers ko pata
 * chalta hai "abhi request kaun kar raha hai" — id + role dono.
 */
public class AuthPrincipal {

    private final Long id;
    private final String username;
    private final String role; // "ADMIN" or "CUSTOMER"

    public AuthPrincipal(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
