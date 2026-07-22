package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Appointment;

import jakarta.transaction.Transactional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Retrieves appointments for a doctor within a time range.
    @Query("""
            SELECT a
            FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.availableTimes
            WHERE d.id = :doctorId
            AND a.appointmentTime BETWEEN :start AND :end
            """)
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end);

    // Retrieves appointments for a doctor and patient name within a time range.
    @Query("""
            SELECT a
            FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH a.patient p
            WHERE d.id = :doctorId
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))
            AND a.appointmentTime BETWEEN :start AND :end
            """)
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId,
            String patientName,
            LocalDateTime start,
            LocalDateTime end);

    // Deletes all appointments for a doctor.
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // Retrieves all appointments for a patient.
    List<Appointment> findByPatientId(Long patientId);

    // Retrieves appointments for a patient with a specific status ordered by time.
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
            Long patientId,
            int status);

    // Filters appointments by doctor name and patient ID.
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
            AND a.patient.id = :patientId
            """)
    List<Appointment> filterByDoctorNameAndPatientId(
            String doctorName,
            Long patientId);

    // Filters appointments by doctor name, patient ID, and status.
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
            AND a.patient.id = :patientId
            AND a.status = :status
            """)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            String doctorName,
            Long patientId,
            int status);

    // Updates the status of an appointment.
    @Modifying
    @Transactional
    @Query("""
            UPDATE Appointment a
            SET a.status = :status
            WHERE a.id = :id
            """)
    void updateStatus(int status, long id);
}