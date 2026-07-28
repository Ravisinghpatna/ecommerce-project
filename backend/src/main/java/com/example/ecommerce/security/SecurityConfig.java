package com.example.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Ye class decide karti hai:
 *  1. Kaunse endpoints bina login ke access ho sakte hain (shopping: products
 *     dekhna, cart use karna, checkout karna)
 *  2. Kaunse endpoints sirf logged-in ADMIN hi access kar sakta hai
 *     (product add/edit/delete)
 *  3. Login kaise hoga (JWT filter, no sessions/cookies)
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Password ko hash karke store/compare karne ke liye (kabhi bhi plain text compare mat karo)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // REST API + JWT use kar rahe hain, cookies/sessions nahi -> CSRF protection ki zaroorat nahi
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session banayenge hi nahi — har request apne token se khud-mukhtar (stateless) hai
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                    // Login/Register endpoints sabke liye khule hain
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/customers/register", "/api/customers/login").permitAll()

                    // Product add/edit/delete sirf ADMIN kar sakta hai
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")
                    
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").authenticated()
                    .requestMatchers("/api/cart/**").authenticated()
                    .requestMatchers("/api/wishlist/**").authenticated()
                    .requestMatchers("/api/orders/**").authenticated()

                    .anyRequest().authenticated()
            )
            // Apna JWT filter Spring Security ke default filter se PEHLE chalate hain
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
