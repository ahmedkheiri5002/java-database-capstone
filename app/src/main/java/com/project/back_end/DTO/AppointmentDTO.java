package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentDTO {

    // 1. Represents the unique identifier for the appointment.
    private Long id;

    // 2. Represents the ID of the doctor.
    private Long doctorId;

    // 3. Represents the doctor's name.
    private String doctorName;

    // 4. Represents the ID of the patient.
    private Long patientId;

    // 5. Represents the patient's name.
    private String patientName;

    // 6. Represents the patient's email.
    private String patientEmail;

    // 7. Represents the patient's phone number.
    private String patientPhone;

    // 8. Represents the patient's address.
    private String patientAddress;

    // 9. Represents the appointment date and time.
    private LocalDateTime appointmentTime;

    // 10. Represents the appointment status.
    private int status;

    // 11. Derived field containing only the appointment date.
    private LocalDate appointmentDate;

    // 12. Derived field containing only the appointment time.
    private LocalTime appointmentTimeOnly;

    // 13. Derived field representing the appointment end time.
    private LocalDateTime endTime;

    // 14. Constructor
    public AppointmentDTO(
            Long id,
            Long doctorId,
            String doctorName,
            Long patientId,
            String patientName,
            String patientEmail,
            String patientPhone,
            String patientAddress,
            LocalDateTime appointmentTime,
            int status) {

        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.appointmentTime = appointmentTime;
        this.status = status;

        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    // 15. Getters

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
