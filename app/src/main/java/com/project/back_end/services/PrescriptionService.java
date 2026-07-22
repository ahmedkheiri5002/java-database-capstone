package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    // Constructor Injection
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Saves a new prescription.
     */
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {

        Map<String, String> response = new HashMap<>();

        try {

            List<Prescription> existing =
                    prescriptionRepository.findByAppointmentId(
                            prescription.getAppointmentId());

            if (!existing.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved successfully.");

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("message", "An internal server error occurred.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves the prescription for a given appointment.
     */
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptions.isEmpty()) {

                response.put("message", "No prescription found.");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.put("prescription", prescriptions.get(0));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("message", "An internal server error occurred.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}