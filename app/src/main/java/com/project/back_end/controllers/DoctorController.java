package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;
    private final TokenService tokenService;

    public DoctorController(DoctorService doctorService, Service service, TokenService tokenService) {
        this.doctorService = doctorService;
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {
        if (!service.validateToken(token, user).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
        }
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, localDate);
            return ResponseEntity.ok(Map.of("doctorId", doctorId, "date", date, "availableSlots", availableSlots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid date format"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(@Valid @RequestBody Doctor doctor,
                                                          @PathVariable String token) {
        if (!service.validateToken(token, "admin").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = doctorService.saveDoctor(doctor);
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Doctor already exists"));
        } else if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to add doctor"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@Valid @RequestBody Login login) {
        Map<String, Object> result = doctorService.validateDoctor(login);
        if (result.containsKey("token")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(@Valid @RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        if (!service.validateToken(token, "admin").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = doctorService.updateDoctor(doctor);
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found"));
        } else if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to update doctor"));
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {
        if (!service.validateToken(token, "admin").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = doctorService.deleteDoctor(id);
        if (result == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Doctor not found"));
        }
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(@PathVariable String name,
                                                      @PathVariable String time,
                                                      @PathVariable String speciality) {
        String n = "null".equalsIgnoreCase(name) ? null : name;
        String t = "null".equalsIgnoreCase(time) ? null : time;
        String s = "null".equalsIgnoreCase(speciality) ? null : speciality;

        List<Doctor> doctors;
        if (n != null && t != null && s != null) {
            doctors = doctorService.filterDoctorsByNameSpecilityandTime(n, s, t);
        } else if (n != null && t != null) {
            doctors = doctorService.filterDoctorByNameAndTime(n, t);
        } else if (n != null && s != null) {
            doctors = doctorService.filterDoctorByNameAndSpecility(n, s);
        } else if (t != null && s != null) {
            doctors = doctorService.filterDoctorByTimeAndSpecility(t, s);
        } else if (n != null) {
            doctors = doctorService.findDoctorByName(n);
        } else if (t != null) {
            doctors = doctorService.filterDoctorsByTime(t);
        } else if (s != null) {
            doctors = doctorService.filterDoctorBySpecility(s);
        } else {
            doctors = doctorService.getDoctors();
        }
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }
}
