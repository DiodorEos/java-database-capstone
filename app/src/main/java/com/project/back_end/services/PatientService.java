package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int createPatient(Patient patient) {
        try {
            if (patient == null || (patient.getEmail() != null && patientRepository.existsByEmail(patient.getEmail()))) {
                return 0;
            }
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private AppointmentDTO toDTO(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor() != null ? a.getDoctor().getId() : null,
                a.getDoctor() != null ? a.getDoctor().getName() : null,
                a.getPatient() != null ? a.getPatient().getId() : null,
                a.getPatient() != null ? a.getPatient().getName() : null,
                a.getPatient() != null ? a.getPatient().getEmail() : null,
                a.getPatient() != null ? a.getPatient().getPhone() : null,
                a.getPatient() != null ? a.getPatient().getAddress() : null,
                a.getAppointmentTime(),
                a.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> apps = appointmentRepository.findByPatientId(patientId);
            return apps.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByCondition(Long patientId, String condition) {
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
        List<Appointment> apps = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
        return apps.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {
        List<Appointment> apps = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);
        return apps.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
        List<Appointment> apps = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
        return apps.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
