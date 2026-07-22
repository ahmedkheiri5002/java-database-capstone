package com.project.back_end.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Constructor Injection
    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // Returns the signing key used for JWT creation and validation.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generates a JWT token valid for 7 days.
    public String generateToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (7L * 24 * 60 * 60 * 1000));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the email (subject) from a JWT token.
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
            .verifyWith((javax.crypto.SecretKey) getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }
    
    // Validates a JWT token against a specific user role.
    public boolean validateToken(String token, String role) {
        try {
            String email = extractEmail(token);

            switch (role.toLowerCase()) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(email);
                    return admin != null;

                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(email);
                    return doctor != null;

                case "patient":
                    Patient patient = patientRepository.findByEmail(email);
                    return patient != null;

                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}