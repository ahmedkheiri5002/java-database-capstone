package com.project.back_end.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

import jakarta.transaction.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Creates a new patient.
     *
     * Returns:
     * 1 = Success
     * 0 = Error
     */
    public int createPatient(Patient patient) {

        try {

            patientRepository.save(patient);

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieves all appointments for a patient and converts them
     * into AppointmentDTO objects.
     */
    @Transactional
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientId);

        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {

            AppointmentDTO dto = new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus());

            appointmentDTOs.add(dto);
        }

        return appointmentDTOs;
    }

    /**
     * Filters appointments by condition (past or future).
     */
    @Transactional
    public List<AppointmentDTO> filterByCondition(
            Long patientId,
            String condition) {

        int status;

        if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else {
            throw new IllegalArgumentException(
                    "Condition must be 'past' or 'future'.");
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                        patientId,
                        status);

        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {

            appointmentDTOs.add(new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()));
        }

        return appointmentDTOs;
    }

    /**
     * Filters appointments by doctor name.
     */
    @Transactional
    public List<AppointmentDTO> filterByDoctor(
            Long patientId,
            String doctorName) {

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientId(
                        doctorName,
                        patientId);

        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {

            appointmentDTOs.add(new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()));
        }

        return appointmentDTOs;
    }

    /**
     * Filters appointments by doctor name and condition.
     */
    @Transactional
    public List<AppointmentDTO> filterByDoctorAndCondition(
            Long patientId,
            String doctorName,
            String condition) {

        int status;

        if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else {
            throw new IllegalArgumentException(
                    "Condition must be 'past' or 'future'.");
        }

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(
                        doctorName,
                        patientId,
                        status);

        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {

            appointmentDTOs.add(new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()));
        }

        return appointmentDTOs;
    }

    /**
     * Retrieves the logged-in patient's details using the JWT token.
     */
    public Patient getPatientDetails(String token) {

        try {

            String email = tokenService.extractEmail(token);

            return patientRepository.findByEmail(email);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method that converts an Appointment entity into an
     * AppointmentDTO.
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {

        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}