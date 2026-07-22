package com.project.back_end.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Constructor Injection
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Validates a JWT token for a given user role.
     */
    public ResponseEntity<Map<String, String>> validateToken(
            String token,
            String role) {

        Map<String, String> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("message", "Authorization token is missing.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.UNAUTHORIZED);
        }

        boolean valid = tokenService.validateToken(token, role);

        if (!valid) {

            response.put("message", "Invalid or expired token.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.UNAUTHORIZED);
        }

        response.put("message", "Token is valid.");

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    /**
     * Validates administrator login credentials.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Admin admin =
                    adminRepository.findByUsername(login.getEmail());

            if (admin == null) {

                response.put("message", "Administrator not found.");

                return new ResponseEntity<>(
                        response,
                        HttpStatus.UNAUTHORIZED);
            }

            if (!admin.getPassword().equals(login.getPassword())) {

                response.put("message", "Invalid password.");

                return new ResponseEntity<>(
                        response,
                        HttpStatus.UNAUTHORIZED);
            }

            String token =
                    tokenService.generateToken(admin.getUsername());

            response.put("message", "Login successful.");
            response.put("token", token);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("message", "Internal server error.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Filters doctors by name, specialty, and available time.
     */
    public List<Doctor> filterDoctor(
            String name,
            String specialty,
            String time) {

        if ((name == null || name.isBlank())
                && (specialty == null || specialty.isBlank())
                && (time == null || time.isBlank())) {

            return doctorService.getDoctors();
        }

        if (!name.isBlank()
                && !specialty.isBlank()
                && !time.isBlank()) {

            return doctorService.filterDoctorsByNameSpecilityandTime(
                    name,
                    specialty,
                    time);
        }

        if (!name.isBlank()
                && !specialty.isBlank()) {

            return doctorService.filterDoctorByNameAndSpecility(
                    name,
                    specialty);
        }

        if (!name.isBlank()
                && !time.isBlank()) {

            return doctorService.filterDoctorByNameAndTime(
                    name,
                    time);
        }

        if (!specialty.isBlank()
                && !time.isBlank()) {

            return doctorService.filterDoctorByTimeAndSpecility(
                    specialty,
                    time);
        }

        if (!name.isBlank()) {
            return doctorService.findDoctorByName(name);
        }

        if (!specialty.isBlank()) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        return doctorService.filterDoctorByTime(null, time);
    }

    /**
     * Validates whether a requested appointment time is available.
     *
     * Returns:
     *  1  -> appointment is valid
     *  0  -> requested time is unavailable
     * -1  -> doctor not found
     */
    public int validateAppointment(
            Long doctorId,
            LocalDateTime appointmentTime) {

        Optional<Doctor> doctor =
                doctorRepository.findById(doctorId);

        if (doctor.isEmpty()) {
            return -1;
        }

        List<String> availableTimes =
                doctorService.getDoctorAvailability(
                        doctorId,
                        appointmentTime.toLocalDate());

        String requestedTime =
                appointmentTime.toLocalTime()
                        .format(DateTimeFormatter.ofPattern("hh:mm a"));

        for (String time : availableTimes) {

            String startTime = time.split("-")[0].trim();

            if (startTime.equalsIgnoreCase(requestedTime)) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Validates that a patient does not already exist.
     */
    public boolean validatePatient(Patient patient) {

        Patient existingPatient =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone());

        return existingPatient == null;
    }

    /**
     * Validates patient login credentials.
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Patient patient = patientRepository.findByEmail(login.getEmail());

            if (patient == null) {

                response.put("message", "Patient not found.");

                return new ResponseEntity<>(
                        response,
                        HttpStatus.UNAUTHORIZED);
            }

            if (!patient.getPassword().equals(login.getPassword())) {

                response.put("message", "Invalid password.");

                return new ResponseEntity<>(
                        response,
                        HttpStatus.UNAUTHORIZED);
            }

            String token = tokenService.generateToken(patient.getEmail());

            response.put("message", "Login successful.");
            response.put("token", token);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("message", "Internal server error.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Filters a patient's appointments based on condition and/or doctor name.
     */
    public List<AppointmentDTO> filterPatient(
            String token,
            String condition,
            String doctorName) {

        Patient patient = patientService.getPatientDetails(token);

        if (patient == null) {
            return new ArrayList<>();
        }

        boolean hasCondition =
                condition != null && !condition.isBlank();

        boolean hasDoctor =
                doctorName != null && !doctorName.isBlank();

        if (hasCondition && hasDoctor) {

            return patientService.filterByDoctorAndCondition(
                    patient.getId(),
                    doctorName,
                    condition);
        }

        if (hasCondition) {

            return patientService.filterByCondition(
                    patient.getId(),
                    condition);
        }

        if (hasDoctor) {

            return patientService.filterByDoctor(
                    patient.getId(),
                    doctorName);
        }

        return patientService.getPatientAppointment(patient.getId());
    }

}
