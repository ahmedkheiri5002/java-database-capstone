/*
 * doctorServices.js
 * Handles all doctor-related API communication.
 */

import { API_BASE_URL } from "../config/config.js";

/* Doctor API Endpoint */
const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Fetch all doctors.
 * @returns {Promise<Array>}
 */
export async function getDoctors() {
    try {

        const response = await fetch(DOCTOR_API);

        const data = await response.json();

        return data.doctors || [];

    } catch (error) {

        console.error("Error fetching doctors:", error);

        return [];

    }
}

/**
 * Delete a doctor.
 * @param {number|string} id
 * @param {string} token
 * @returns {Promise<Object>}
 */
export async function deleteDoctor(id, token) {
    try {

        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE"
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message
        };

    } catch (error) {

        console.error("Error deleting doctor:", error);

        return {
            success: false,
            message: "Unable to delete doctor."
        };

    }
}

/**
 * Save a new doctor.
 * @param {Object} doctor
 * @param {string} token
 * @returns {Promise<Object>}
 */
export async function saveDoctor(doctor, token) {
    try {

        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message
        };

    } catch (error) {

        console.error("Error saving doctor:", error);

        return {
            success: false,
            message: "Unable to save doctor."
        };

    }
}

/**
 * Filter doctors by name, time, and specialty.
 * @param {string} name
 * @param {string} time
 * @param {string} specialty
 * @returns {Promise<Object>}
 */
export async function filterDoctors(name, time, specialty) {
    try {

        const doctorName = name || "";
        const appointmentTime = time || "";
        const doctorSpecialty = specialty || "";

        const response = await fetch(
            `${DOCTOR_API}/filter/${doctorName}/${appointmentTime}/${doctorSpecialty}`
        );

        if (response.ok) {

            return await response.json();

        }

        console.error("Failed to filter doctors.");

        return {
            doctors: []
        };

    } catch (error) {

        console.error("Error filtering doctors:", error);

        alert("Failed to retrieve filtered doctors.");

        return {
            doctors: []
        };

    }
}
