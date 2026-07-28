package com.example.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * OncePerRequestFilter -> ye filter HAR incoming HTTP request ke liye ek
 * baar chalta hai, controller tak pahunchne se pehle.
 *
 * Kaam: Authorization header check karo -> "Bearer <token>" nikaalo ->
 * verify karo -> agar valid hai to Spring Security ko bata do "ye request
 * authenticated hai" (SecurityContextHolder me daal ke), taaki
 * @PreAuthorize / SecurityConfig ke rules apply ho sakein.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " ke baad ka part

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                Long userId = jwtUtil.extractUserId(token);

                AuthPrincipal principal = new AuthPrincipal(userId, username, role);

                // authorities me "ROLE_" prefix Spring Security ka convention hai —
                // hasRole("ADMIN") likhne par ye khud "ROLE_ADMIN" dhoondhta hai.
                var authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // request ko aage badhao
    }
}
