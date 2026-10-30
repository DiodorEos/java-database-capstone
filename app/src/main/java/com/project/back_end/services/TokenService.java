package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Component
public class TokenService {
    private static final String ISSUER = "smart-clinic";
    private static final Set<String> ROLES = Set.of("admin", "doctor", "patient");
    private final SecretKey key;
    private final AdminRepository admins;
    private final DoctorRepository doctors;
    private final PatientRepository patients;

    public TokenService(@Value("${jwt.secret}") String secret, AdminRepository admins,
                        DoctorRepository doctors, PatientRepository patients) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.admins = admins;
        this.doctors = doctors;
        this.patients = patients;
    }

    public SecretKey getSigningKey() {
        return this.key;
    }

    // Call only AFTER credentials have been verified by the future login service.
    public String generateToken(String identity, String role) {
        if (identity == null || identity.isBlank() || !ROLES.contains(role)) {
            throw new IllegalArgumentException("A valid identity and role are required");
        }
        Instant now = Instant.now();
        return Jwts.builder().issuer(ISSUER).subject(identity).claim("role", role)
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    private Claims parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build()
                .parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null || claims.getSubject() == null
                || claims.getSubject().isBlank()) {
            throw new IllegalArgumentException("Token lacks required claims");
        }
        return claims;
    }

    public String extractEmail(String token) {
        // For admins the subject is a username, not an email.
        return parse(token).getSubject();
    }

    public boolean validateToken(String token, String role) {
        if (token == null || token.isBlank() || role == null || !ROLES.contains(role)) return false;
        final Claims claims;
        try {
            claims = parse(token);
            if (!role.equals(claims.get("role", String.class))) return false;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
        // Account removal invalidates view access even before the token expires.
        return switch (role) {
            case "admin" -> admins.existsByUsername(claims.getSubject());
            case "doctor" -> doctors.existsByEmail(claims.getSubject());
            case "patient" -> patients.existsByEmail(claims.getSubject());
            default -> false;
        };
    }
}
