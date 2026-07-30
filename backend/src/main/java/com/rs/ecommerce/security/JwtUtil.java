package com.rs.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT (JSON Web Token) ka basic idea:
 * - Login successful hote hi hum ek "token" bana kar client ko dete hain.
 * - Token ke andar username aur expiry time encoded hota hai, aur ye
 *   humare secret key se "signed" hota hai (tamper-proof — koi bhi
 *   client-side se token ka content badal nahi sakta bina secret jaane).
 * - Har agli request me client Authorization header me
 *   "Bearer <token>" bhejta hai, hum usse verify karke pehchan lete hain
 *   ki request kaun kar raha hai — server ko session store karne ki
 *   zaroorat nahi padti (stateless authentication).
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Token generate karte waqt sirf username nahi, role (ADMIN/CUSTOMER) aur
     * database id bhi andar daal dete hain — taaki har request pe hume pata
     * chal jaaye "ye kaun hai" aur "iska role kya hai", bina dobara database
     * query kiye.
     */
    public String generateToken(String username, String role, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Signature invalid, token expired, malformed, etc.
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
