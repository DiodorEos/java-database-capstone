package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 Service service,
                                 TokenService tokenService,
                                 DoctorRepository doctorRepository) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/{date}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String name,
                                                               @PathVariable String token) {
        if (!service.validateToken(token, "doctor").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
        }
        try {
            String email = tokenService.extractEmail(token);
            Doctor doctor = doctorRepository.findByEmail(email);
            Long doctorId = doctor != null ? doctor.getId() : 1L;
            LocalDate localDate = LocalDate.parse(date);
            String patientName = "null".equalsIgnoreCase(name) ? null : name;

            List<Appointment> appointments = appointmentService.getAppointments(doctorId, localDate, patientName);
            return ResponseEntity.ok(Map.of("appointments", appointments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error fetching appointments"));
        }
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        if (!service.validateToken(token, "patient").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Failed to book appointment"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(@Valid @RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        if (!service.validateToken(token, "patient").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = appointmentService.updateAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Appointment updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Failed to update appointment"));
    }

    @DeleteMapping("/{appointmentId}/{patientId}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long appointmentId,
                                                                 @PathVariable Long patientId,
                                                                 @PathVariable String token) {
        if (!service.validateToken(token, "patient").isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }
        int result = appointmentService.cancelAppointment(appointmentId, patientId);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Failed to cancel appointment"));
    }
}
