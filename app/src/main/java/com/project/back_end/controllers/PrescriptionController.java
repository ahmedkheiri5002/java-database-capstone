package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    // Constructor Injection
    public PrescriptionController(
            PrescriptionService prescriptionService,
            Service service,
            AppointmentService appointmentService) {

        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    /**
     * Saves a prescription for an appointment.
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        // Mark the appointment as completed/prescription added.
        appointmentService.changeStatus(
                prescription.getAppointmentId(),
                1);

        return prescriptionService.savePrescription(prescription);
    }

    /**
     * Retrieves the prescription associated with an appointment.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        return prescriptionService.getPrescription(appointmentId);
    }

}
