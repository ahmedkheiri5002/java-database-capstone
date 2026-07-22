package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    // Constructor Injection
    public DoctorController(
            DoctorService doctorService,
            Service service) {

        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * Returns a doctor's available appointment times.
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        Map<String, Object> response = new HashMap<>();
        response.put(
                "availability",
                doctorService.getDoctorAvailability(doctorId, date));

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all doctors.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new doctor.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);

        Map<String, String> response = new HashMap<>();

        switch (result) {

            case 1:
                response.put("message", "Doctor added successfully.");
                return ResponseEntity.ok(response);

            case -1:
                response.put("message", "Doctor already exists.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);

            default:
                response.put("message", "Unable to save doctor.");
                return new ResponseEntity<>(
                        response,
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Doctor login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @Valid @RequestBody Login login) {

        return doctorService.validateDoctor(login);
    }

    /**
     * Updates an existing doctor.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        int result = doctorService.updateDoctor(doctor);

        Map<String, String> response = new HashMap<>();

        switch (result) {

            case 1:
                response.put("message", "Doctor updated successfully.");
                return ResponseEntity.ok(response);

            case -1:
                response.put("message", "Doctor not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

            default:
                response.put("message", "Unable to update doctor.");
                return new ResponseEntity<>(
                        response,
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a doctor.
     */
    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        int result = doctorService.deleteDoctor(doctorId);

        Map<String, String> response = new HashMap<>();

        switch (result) {

            case 1:
                response.put("message", "Doctor deleted successfully.");
                return ResponseEntity.ok(response);

            case -1:
                response.put("message", "Doctor not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

            default:
                response.put("message", "Unable to delete doctor.");
                return new ResponseEntity<>(
                        response,
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Filters doctors by name, time, and specialty.
     */
    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialty) {

        List<Doctor> doctors =
                service.filterDoctor(name, specialty, time);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);

        return ResponseEntity.ok(response);
    }

}