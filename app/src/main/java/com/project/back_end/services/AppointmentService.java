package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            if (appointment == null) return 0;
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        try {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            if (patientName != null && !patientName.isBlank() && !"null".equalsIgnoreCase(patientName)) {
                return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctorId, patientName.trim(), start, end);
            } else {
                return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public int updateAppointment(Appointment appointment) {
        try {
            if (appointment == null || appointment.getId() == null || !appointmentRepository.existsById(appointment.getId())) {
                return 0;
            }
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int cancelAppointment(Long appointmentId, Long patientId) {
        try {
            return appointmentRepository.findById(appointmentId).map(app -> {
                if (app.getPatient() != null && app.getPatient().getId().equals(patientId)) {
                    appointmentRepository.deleteById(appointmentId);
                    return 1;
                }
                return 0;
            }).orElse(0);
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}
