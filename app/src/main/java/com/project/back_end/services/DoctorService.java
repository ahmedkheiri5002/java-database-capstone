package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

import jakarta.transaction.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor Injection
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Retrieves all available appointment times for a doctor on a given date.
     * Already-booked times are removed from the doctor's availability list.
     */
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

        if (doctorOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOptional.get();

        List<String> availableTimes = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        start,
                        end);

        for (Appointment appointment : appointments) {

            LocalTime bookedTime =
                    appointment.getAppointmentTime().toLocalTime();

            availableTimes.removeIf(time -> time.contains(bookedTime.toString()));
        }

        return availableTimes;
    }

    /**
     * Saves a new doctor.
     *
     * Returns:
     *  1  = Success
     * -1  = Email already exists
     *  0  = Internal error
     */
    public int saveDoctor(Doctor doctor) {

        try {

            Doctor existing =
                    doctorRepository.findByEmail(doctor.getEmail());

            if (existing != null) {
                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates an existing doctor.
     *
     * Returns:
     *  1  = Success
     * -1  = Doctor not found
     *  0  = Internal error
     */
    public int updateDoctor(Doctor doctor) {

        try {

            Optional<Doctor> existing =
                    doctorRepository.findById(doctor.getId());

            if (existing.isEmpty()) {
                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Retrieves all doctors.
     */
    @Transactional
    public List<Doctor> getDoctors() {

        return doctorRepository.findAll();
    }

    /**
     * Deletes a doctor and all appointments associated with that doctor.
     *
     * Returns:
     *  1  = Success
     * -1  = Doctor not found
     *  0  = Internal error
     */
    public int deleteDoctor(Long doctorId) {

        try {

            Optional<Doctor> doctorOptional =
                    doctorRepository.findById(doctorId);

            if (doctorOptional.isEmpty()) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.delete(doctorOptional.get());

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Validates a doctor's login credentials.
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Doctor doctor =
                    doctorRepository.findByEmail(login.getEmail());

            if (doctor == null) {
                response.put("message", "Doctor not found.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            String token =
                    tokenService.generateToken(doctor.getEmail());

            response.put("message", "Login successful.");
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {

            response.put("message", "Internal server error.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Finds doctors whose names partially match the supplied name.
     */
    @Transactional
    public List<Doctor> findDoctorByName(String name) {

        if (name == null || name.isBlank()) {
            return doctorRepository.findAll();
        }

        return doctorRepository.findByNameLike("%" + name + "%");
    }

    /**
     * Filters doctors by name, specialty, and AM/PM availability.
     */
    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name,
                        specialty);

        return filterDoctorByTime(doctors, time);
    }

    /**
     * Filters a list of doctors based on AM/PM availability.
     */
    @Transactional
    public List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String time) {

        if (time == null || time.isBlank()) {
            return doctors;
        }

        List<Doctor> filteredDoctors = new ArrayList<>();

        for (Doctor doctor : doctors) {

            boolean match = false;

            for (String availableTime : doctor.getAvailableTimes()) {

                String value = availableTime.toUpperCase();

                if ("AM".equalsIgnoreCase(time) && value.contains("AM")) {
                    match = true;
                    break;
                }

                if ("PM".equalsIgnoreCase(time) && value.contains("PM")) {
                    match = true;
                    break;
                }
            }

            if (match) {
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

    /**
     * Filters doctors by name and AM/PM availability.
     */
    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(
            String name,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findByNameLike("%" + name + "%");

        return filterDoctorByTime(doctors, time);
    }

    /**
     * Filters doctors by name and specialty.
     */
    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(
            String name,
            String specialty) {

        return doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name,
                        specialty);
    }

    /**
     * Filters doctors by specialty and AM/PM availability.
     */
    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(
            String specialty,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specialty);

        return filterDoctorByTime(doctors, time);
    }

    /**
     * Filters doctors by specialty.
     */
    @Transactional
    public List<Doctor> filterDoctorBySpecility(
            String specialty) {

        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    /**
     * Filters all doctors by AM/PM availability.
     */
    @Transactional
    public List<Doctor> filterDoctorsByTime(String time) {

        List<Doctor> doctors = doctorRepository.findAll();

        return filterDoctorByTime(doctors, time);
    }

}
