package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Retrieves a doctor by email.
    Doctor findByEmail(String email);

    // Retrieves doctors whose name contains the given search string.
    List<Doctor> findByNameLike(String name);

    // Retrieves doctors whose name contains the search string and specialty matches (case-insensitive).
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            String name,
            String specialty);

    // Retrieves doctors by specialty (case-insensitive).
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

}