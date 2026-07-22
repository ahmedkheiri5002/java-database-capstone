package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // Constructor Injection
    public AppointmentController(
            AppointmentService appointmentService,
            Service service) {

        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Retrieves appointments for a doctor on a given date,
     * optionally filtered by patient name.
     */
    @GetMapping("/{doctorId}/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        List<AppointmentDTO> appointments =
                appointmentService.getAppointments(
                        doctorId,
                        date,
                        patientName);

        return ResponseEntity.ok(appointments);
    }

    /**
     * Books a new appointment.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {

            return ResponseEntity.ok(
                    Map.of("message", "Appointment booked successfully."));
        }

        return ResponseEntity.badRequest().body(
                Map.of("message",
                        "Unable to book appointment. The doctor may not exist or the selected time is unavailable."));
    }

    /**
     * Updates an existing appointment.
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @Valid @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        String message =
                appointmentService.updateAppointment(
                        token,
                        appointment);

        if ("Appointment updated successfully.".equals(message)) {

            return ResponseEntity.ok(
                    Map.of("message", message));
        }

        return ResponseEntity.badRequest().body(
                Map.of("message", message));
    }

    /**
     * Cancels an appointment.
     */
    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().equals(HttpStatus.OK)) {
            return validation;
        }

        String message =
                appointmentService.cancelAppointment(
                        token,
                        appointmentId);

        if ("Appointment cancelled successfully.".equals(message)) {

            return ResponseEntity.ok(
                    Map.of("message", message));
        }

        return ResponseEntity.badRequest().body(
                Map.of("message", message));
    }

}