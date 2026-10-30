package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByEmail(String email);
    boolean existsByEmail(String email);
    List<Doctor> findByNameContainingIgnoreCase(String name);
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);
}
