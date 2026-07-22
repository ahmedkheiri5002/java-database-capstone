package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    // Constructor Injection
    public PatientController(
            PatientService patientService,
            Service service) {

        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Retrieves the logged-in patient's details.
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        Patient patient = patientService.getPatientDetails(token);

        Map<String, Object> response = new HashMap<>();
        response.put("patient", patient);

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new patient.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(
            @Valid @RequestBody Patient patient) {

        Map<String, String> response = new HashMap<>();

        if (!service.validatePatient(patient)) {

            response.put("message",
                    "A patient with the same email or phone number already exists.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT);
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {

            response.put("message", "Patient created successfully.");

            return ResponseEntity.ok(response);
        }

        response.put("message", "Unable to create patient.");

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Patient login.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody Login login) {

        return service.validatePatientLogin(login);
    }

    /**
     * Retrieves appointments for a patient.
     */
    @GetMapping("/appointments/{patientId}/{user}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable String user,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        List<AppointmentDTO> appointments =
                patientService.getPatientAppointment(patientId);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);

        return ResponseEntity.ok(response);
    }

    /**
     * Filters a patient's appointments.
     */
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        List<AppointmentDTO> appointments =
                service.filterPatient(
                        token,
                        condition,
                        name);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);

        return ResponseEntity.ok(response);
    }

}
