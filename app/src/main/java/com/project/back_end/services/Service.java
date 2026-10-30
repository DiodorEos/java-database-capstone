package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Service {
    private final TokenService tokens;
    private final AdminRepository adminRepository;
    private final PatientRepository patientRepository;

    public Service(TokenService tokens, AdminRepository adminRepository, PatientRepository patientRepository) {
        this.tokens = tokens;
        this.adminRepository = adminRepository;
        this.patientRepository = patientRepository;
    }

    public Map<String, Object> validateToken(String token, String role) {
        return tokens.validateToken(token, role)
                ? Map.of()
                : Map.of("message", "Invalid or expired token", "status", 401);
    }

    public Map<String, Object> validateAdmin(Admin admin) {
        Map<String, Object> response = new HashMap<>();
        if (admin == null || admin.getUsername() == null) {
            response.put("message", "Invalid credentials");
            return response;
        }
        Admin existing = adminRepository.findByUsername(admin.getUsername());
        if (existing == null) {
            response.put("message", "Admin not found");
            return response;
        }
        String token = tokens.generateToken(existing.getUsername(), "admin");
        response.put("token", token);
        return response;
    }

    public Map<String, Object> validatePatientLogin(Login login) {
        Map<String, Object> response = new HashMap<>();
        if (login == null || login.getEmail() == null) {
            response.put("message", "Invalid credentials");
            return response;
        }
        Patient patient = patientRepository.findByEmail(login.getEmail());
        if (patient == null) {
            response.put("message", "Patient not found");
            return response;
        }
        String token = tokens.generateToken(patient.getEmail(), "patient");
        response.put("token", token);
        response.put("patient", patient);
        return response;
    }

    public Patient getPatientByToken(String token) {
        try {
            String email = tokens.extractEmail(token);
            return patientRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
}
