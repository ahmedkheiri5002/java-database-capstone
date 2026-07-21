/*
 * doctorCard.js
 */

import { showBookingOverlay } from "./loggedPatient.js";
import { deleteDoctor } from "./doctorServices.js";
import { getPatientDetails } from "./patientServices.js";

/**
 * Creates and returns a doctor card element.
 * @param {Object} doctor
 * @returns {HTMLElement}
 */
export function createDoctorCard(doctor) {

    /* Create Card */
    const card = document.createElement("div");
    card.className = "doctor-card";

    /* Current User Role */
    const role = localStorage.getItem("userRole");

    /* Doctor Information Container */
    const doctorInfo = document.createElement("div");
    doctorInfo.className = "doctor-info";

    /* Doctor Name */
    const doctorName = document.createElement("h3");
    doctorName.textContent = doctor.name;

    /* Doctor Specialization */
    const specialization = document.createElement("p");
    specialization.textContent =
        `Specialization: ${doctor.specialization}`;

    /* Doctor Email */
    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    /* Available Appointment Times */
    const appointmentTimes = document.createElement("p");

    appointmentTimes.textContent =
        `Available Times: ${
            doctor.availableTimes?.join(", ") || "None Available"
        }`;

    /* Append Information */
    doctorInfo.appendChild(doctorName);
    doctorInfo.appendChild(specialization);
    doctorInfo.appendChild(email);
    doctorInfo.appendChild(appointmentTimes);

    /* Actions Container */
    const actions = document.createElement("div");
    actions.className = "doctor-card-actions";

    /* ===========================
       ADMIN ACTIONS
    ============================ */

    if (role === "admin") {

        const deleteButton = document.createElement("button");
        deleteButton.className = "adminBtn";
        deleteButton.textContent = "Delete";

        deleteButton.addEventListener("click", async () => {

            const token = localStorage.getItem("token");

            try {

                await deleteDoctor(doctor.id, token);

                alert("Doctor deleted successfully.");

                card.remove();

            } catch (error) {

                console.error(error);
                alert("Failed to delete doctor.");

            }

        });

        actions.appendChild(deleteButton);
    }

    /* ===========================
       PATIENT (NOT LOGGED IN)
    ============================ */

    else if (role === "patient") {

        const bookButton = document.createElement("button");
        bookButton.textContent = "Book Now";

        bookButton.addEventListener("click", () => {
            alert("Please log in before booking an appointment.");
        });

        actions.appendChild(bookButton);
    }

    /* ===========================
       LOGGED-IN PATIENT
    ============================ */

    else if (role === "loggedPatient") {

        const bookButton = document.createElement("button");
        bookButton.textContent = "Book Now";

        bookButton.addEventListener("click", async () => {

            const token = localStorage.getItem("token");

            if (!token) {
                window.location.href = "/";
                return;
            }

            try {

                const patient = await getPatientDetails(token);

                showBookingOverlay(doctor, patient);

            } catch (error) {

                console.error(error);
                alert("Unable to load patient information.");

            }

        });

        actions.appendChild(bookButton);
    }

    /* Build Card */
    card.appendChild(doctorInfo);
    card.appendChild(actions);

    return card;
}
