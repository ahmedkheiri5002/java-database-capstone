/*
 * doctorDashboard.js
 * Handles loading and filtering doctor appointments.
 */

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

/* ==========================================
   Global Variables
========================================== */

const patientTableBody = document.getElementById("patientTableBody");

let selectedDate = new Date().toISOString().split("T")[0];

const token = localStorage.getItem("token");

let patientName = null;

/* ==========================================
   Search Bar
========================================== */

document
    .getElementById("searchBar")
    ?.addEventListener("input", (event) => {

        const value = event.target.value.trim();

        patientName = value !== "" ? value : "null";

        loadAppointments();

    });

/* ==========================================
   Today's Appointments Button
========================================== */

document
    .getElementById("todayButton")
    ?.addEventListener("click", () => {

        selectedDate = new Date().toISOString().split("T")[0];

        document.getElementById("datePicker").value = selectedDate;

        loadAppointments();

    });

/* ==========================================
   Date Picker
========================================== */

document
    .getElementById("datePicker")
    ?.addEventListener("change", (event) => {

        selectedDate = event.target.value;

        loadAppointments();

    });

/* ==========================================
   Load Appointments
========================================== */

async function loadAppointments() {

    try {

        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        patientTableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {

            patientTableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No Appointments found for today.
                    </td>
                </tr>
            `;

            return;
        }

        appointments.forEach((appointment) => {

            const patient = {
                id: appointment.patient.id,
                name: appointment.patient.name,
                phone: appointment.patient.phone,
                email: appointment.patient.email
            };

            const row = createPatientRow(patient, appointment);

            patientTableBody.appendChild(row);

        });

    } catch (error) {

        console.error(error);

        patientTableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;

    }

}

/* ==========================================
   Initial Page Load
========================================== */

document.addEventListener("DOMContentLoaded", () => {

    if (typeof renderContent === "function") {
        renderContent();
    }

    document.getElementById("datePicker").value = selectedDate;

    loadAppointments();

});
