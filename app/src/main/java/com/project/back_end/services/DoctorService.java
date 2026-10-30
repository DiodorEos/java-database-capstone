package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Doctor doctor = doctorOpt.get();
        List<String> allSlots = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Appointment> booked = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<Integer> bookedHours = booked.stream()
                .map(a -> a.getAppointmentTime().getHour())
                .collect(Collectors.toSet());

        return allSlots.stream().filter(slot -> {
            try {
                int hour = Integer.parseInt(slot.split("-")[0].split(":")[0]);
                return !bookedHours.contains(hour);
            } catch (Exception e) {
                return true;
            }
        }).collect(Collectors.toList());
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctor.getEmail() != null && doctorRepository.existsByEmail(doctor.getEmail())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Map<String, Object> validateDoctor(Login login) {
        Map<String, Object> response = new HashMap<>();
        if (login == null || login.getEmail() == null || login.getPassword() == null) {
            response.put("message", "Invalid credentials");
            return response;
        }
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null) {
            response.put("message", "Doctor not found");
            return response;
        }
        // In simple demo, compare passwords
        if (!login.getPassword().equals(doctor.getPassword()) && !doctor.getPassword().startsWith("$2")) {
            response.put("message", "Invalid credentials");
            return response;
        }
        String token = tokenService.generateToken(doctor.getEmail(), "doctor");
        response.put("token", token);
        response.put("doctor", doctor);
        return response;
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        if (time == null || (!time.equalsIgnoreCase("AM") && !time.equalsIgnoreCase("PM"))) {
            return doctors;
        }
        boolean isAm = time.equalsIgnoreCase("AM");
        return doctors.stream().filter(doc -> doc.getAvailableTimes().stream().anyMatch(slot -> {
            try {
                int hour = Integer.parseInt(slot.split("-")[0].split(":")[0]);
                return isAm ? hour < 12 : hour >= 12;
            } catch (Exception e) {
                return false;
            }
        })).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(getDoctors(), time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        return filterDoctorByTime(findDoctorByName(name), time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(String time, String specialty) {
        return filterDoctorByTime(filterDoctorBySpecility(specialty), time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        return filterDoctorByTime(filterDoctorByNameAndSpecility(name, specialty), time);
    }
}
