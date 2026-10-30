package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized access"));
        }
        Patient patient = service.getPatientByToken(token);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Patient not found"));
        }
        return ResponseEntity.ok(Map.of("patient", patient));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody Patient patient) {
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Patient registered successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Patient registration failed"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody Login login) {
        Map<String, Object> result = service.validatePatientLogin(login);
        if (result.containsKey("token")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id,
                                                                    @PathVariable String user,
                                                                    @PathVariable String token) {
        if (!service.validateToken(token, user).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        List<AppointmentDTO> appointments = patientService.getPatientAppointment(id);
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        if (!service.validateToken(token, "patient").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        Patient patient = service.getPatientByToken(token);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Patient not found"));
        }
        String c = "null".equalsIgnoreCase(condition) ? null : condition;
        String n = "null".equalsIgnoreCase(name) ? null : name;

        List<AppointmentDTO> appointments;
        if (c != null && n != null) {
            appointments = patientService.filterByDoctorAndCondition(patient.getId(), n, c);
        } else if (c != null) {
            appointments = patientService.filterByCondition(patient.getId(), c);
        } else if (n != null) {
            appointments = patientService.filterByDoctor(patient.getId(), n);
        } else {
            appointments = patientService.getPatientAppointment(patient.getId());
        }
        return ResponseEntity.ok(Map.of("appointments", appointments));
    }
}
