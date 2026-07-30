package com.rs.ecommerce.security;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Har request ke andar JwtAuthFilter ne pehle hi "AuthPrincipal" ko
 * SecurityContext me daal diya hota hai (agar token valid tha).
 * Ye helper class controllers ke liye ek shortcut hai — ek line likho
 * aur pata chal jaaye "abhi request kaun kar raha hai" (id, username, role).
 */
public class CurrentUser {

    public static AuthPrincipal get() {
        return (AuthPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long id() {
        return get().getId();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(get().getRole());
    }
}
