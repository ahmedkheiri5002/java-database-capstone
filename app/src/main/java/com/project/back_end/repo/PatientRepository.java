package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Retrieves a patient by email.
    Patient findByEmail(String email);

    // Retrieves a patient by either email or phone number.
    Patient findByEmailOrPhone(String email, String phone);

}