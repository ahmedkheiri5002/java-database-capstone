package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Constructor Injection
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            Service service,
            TokenService tokenService,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Books a new appointment.
     *
     * Returns:
     * 1 -> Appointment booked successfully
     * 0 -> Failed to book appointment
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {

        try {

            Optional<Doctor> doctor =
                    doctorRepository.findById(
                            appointment.getDoctor().getId());

            Optional<Patient> patient =
                    patientRepository.findById(
                            appointment.getPatient().getId());

            if (doctor.isEmpty() || patient.isEmpty()) {
                return 0;
            }

            int validAppointment =
                    service.validateAppointment(
                            doctor.get().getId(),
                            appointment.getAppointmentTime());

            if (validAppointment != 1) {
                return 0;
            }

            appointment.setDoctor(doctor.get());
            appointment.setPatient(patient.get());

            appointmentRepository.save(appointment);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Updates an existing appointment.
     *
     * Returns:
     * - "Appointment updated successfully."
     * - "Appointment not found."
     * - "Patient is not authorized to update this appointment."
     * - "Doctor is not available at the requested time."
     * - "Doctor or patient not found."
     * - "Internal server error."
     */
    @Transactional
    public String updateAppointment(String token, Appointment updatedAppointment) {

        try {

            Patient loggedInPatient = patientRepository.findByEmail(
                    tokenService.extractEmail(token));

            if (loggedInPatient == null) {
                return "Patient not found.";
            }

            Optional<Appointment> existingAppointment =
                    appointmentRepository.findById(updatedAppointment.getId());

            if (existingAppointment.isEmpty()) {
                return "Appointment not found.";
            }

            Appointment appointment = existingAppointment.get();

            // Ensure the logged-in patient owns the appointment
            if (!appointment.getPatient().getId().equals(loggedInPatient.getId())) {
                return "Patient is not authorized to update this appointment.";
            }

            Optional<Doctor> doctor =
                    doctorRepository.findById(updatedAppointment.getDoctor().getId());

            Optional<Patient> patient =
                    patientRepository.findById(loggedInPatient.getId());

            if (doctor.isEmpty() || patient.isEmpty()) {
                return "Doctor or patient not found.";
            }

            int validAppointment = service.validateAppointment(
                    doctor.get().getId(),
                    updatedAppointment.getAppointmentTime());

            if (validAppointment != 1) {
                return "Doctor is not available at the requested time.";
            }

            appointment.setDoctor(doctor.get());
            appointment.setPatient(patient.get());
            appointment.setAppointmentTime(updatedAppointment.getAppointmentTime());

            // Preserve existing status unless your application allows changing it
            appointmentRepository.save(appointment);

            return "Appointment updated successfully.";

        } catch (Exception e) {

            e.printStackTrace();
            return "Internal server error.";
        }
    }

    /**
     * Cancels an appointment.
     *
     * Returns:
     * - "Appointment cancelled successfully."
     * - "Appointment not found."
     * - "Patient is not authorized to cancel this appointment."
     * - "Patient not found."
     * - "Internal server error."
     */
    @Transactional
    public String cancelAppointment(String token, Long appointmentId) {

        try {

            Patient loggedInPatient =
                    patientRepository.findByEmail(
                            tokenService.extractEmail(token));

            if (loggedInPatient == null) {
                return "Patient not found.";
            }

            Optional<Appointment> appointmentOptional =
                    appointmentRepository.findById(appointmentId);

            if (appointmentOptional.isEmpty()) {
                return "Appointment not found.";
            }

            Appointment appointment = appointmentOptional.get();

            // Ensure the logged-in patient owns the appointment
            if (!appointment.getPatient().getId().equals(loggedInPatient.getId())) {
                return "Patient is not authorized to cancel this appointment.";
            }

            appointmentRepository.delete(appointment);

            return "Appointment cancelled successfully.";

        } catch (Exception e) {

            e.printStackTrace();
            return "Internal server error.";
        }
    }

    /**
     * Retrieves appointments for a doctor on a given date,
     * optionally filtered by patient name.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointments(
            Long doctorId,
            LocalDate date,
            String patientName) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;

        if (patientName == null || patientName.isBlank()) {

            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                            doctorId,
                            startOfDay,
                            endOfDay);

        } else {

            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId,
                            patientName,
                            startOfDay,
                            endOfDay);
        }

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
     * Changes the status of an appointment.
     *
     * Returns:
     * 1 -> Success
     * 0 -> Failure
     */
    @Transactional
    public int changeStatus(Long appointmentId, Integer status) {

        try {

            appointmentRepository.updateStatus(
                    appointmentId,
                    status);

            return 1;

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }
}