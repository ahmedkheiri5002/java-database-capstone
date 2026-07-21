/*
 * adminDashboard.js
 * Handles doctor management for the Admin Dashboard.
 */

import { openModal } from "../components/modals.js";
import {
    getDoctors,
    filterDoctors,
    saveDoctor
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

/* ==========================
   Add Doctor Button
========================== */

const addDoctorButton = document.getElementById("addDocBtn");

if (addDoctorButton) {
    addDoctorButton.addEventListener("click", () => {
        openModal("addDoctor");
    });
}

/* ==========================
   Initial Page Load
========================== */

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
});

/* ==========================
   Load All Doctors
========================== */

async function loadDoctorCards() {
    try {

        const doctors = await getDoctors();

        renderDoctorCards(doctors);

    } catch (error) {

        console.error("Failed to load doctors:", error);

    }
}

/* ==========================
   Search & Filter Listeners
========================== */

document
    .getElementById("searchBar")
    ?.addEventListener("input", filterDoctorsOnChange);

document
    .getElementById("filterTime")
    ?.addEventListener("change", filterDoctorsOnChange);

document
    .getElementById("filterSpecialty")
    ?.addEventListener("change", filterDoctorsOnChange);

/* ==========================
   Filter Doctors
========================== */

async function filterDoctorsOnChange() {

    try {

        const name =
            document.getElementById("searchBar")?.value.trim() || null;

        const time =
            document.getElementById("filterTime")?.value || null;

        const specialty =
            document.getElementById("filterSpecialty")?.value || null;

        const result = await filterDoctors(
            name,
            time,
            specialty
        );

        const doctors = result.doctors || result;

        if (doctors.length > 0) {

            renderDoctorCards(doctors);

        } else {

            const contentDiv = document.getElementById("content");

            contentDiv.innerHTML =
                "<p>No doctors found with the given filters.</p>";

        }

    } catch (error) {

        console.error(error);
        alert("Unable to filter doctors.");

    }
}

/* ==========================
   Render Doctor Cards
========================== */

function renderDoctorCards(doctors) {

    const contentDiv = document.getElementById("content");

    contentDiv.innerHTML = "";

    doctors.forEach((doctor) => {
        contentDiv.appendChild(createDoctorCard(doctor));
    });
}

/* ==========================
   Add Doctor
========================== */

window.adminAddDoctor = async function () {

    const token = localStorage.getItem("token");

    if (!token) {
        alert("You must be logged in as an admin.");
        return;
    }

    const availableTimes = [];

    document
        .querySelectorAll("input[name='availability']:checked")
        .forEach((checkbox) => {
            availableTimes.push(checkbox.value);
        });

    const doctor = {
        name: document.getElementById("doctorName").value,
        email: document.getElementById("doctorEmail").value,
        phone: document.getElementById("doctorPhone").value,
        password: document.getElementById("doctorPassword").value,
        specialization: document.getElementById("doctorSpecialty").value,
        availableTimes: availableTimes
    };

    try {

        const result = await saveDoctor(doctor, token);

        if (result.success) {

            alert("Doctor added successfully.");

            document.getElementById("modal").style.display = "none";

            loadDoctorCards();

        } else {

            alert(result.message);

        }

    } catch (error) {

        console.error(error);
        alert("Failed to add doctor.");

    }
};
