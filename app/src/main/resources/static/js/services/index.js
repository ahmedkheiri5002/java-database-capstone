/*
 * index.js
 * Handles role-based login functionality.
 */

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

/* API Endpoints */
const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

/* Attach Event Listeners */
window.onload = function () {

    const adminBtn = document.getElementById("adminLogin");
    const doctorBtn = document.getElementById("doctorLogin");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }

};

/**
 * Handles Admin Login
 */
window.adminLoginHandler = async function () {

    try {

        /* Get Credentials */
        const username = document.getElementById("adminUsername").value;
        const password = document.getElementById("adminPassword").value;

        /* Create Request Object */
        const admin = {
            username,
            password
        };

        /* Send Login Request */
        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("admin");

        } else {

            alert("Invalid credentials!");

        }

    } catch (error) {

        console.error(error);
        alert("Something went wrong. Please try again.");

    }

};

/**
 * Handles Doctor Login
 */
window.doctorLoginHandler = async function () {

    try {

        /* Get Credentials */
        const email = document.getElementById("doctorEmail").value;
        const password = document.getElementById("doctorPassword").value;

        /* Create Request Object */
        const doctor = {
            email,
            password
        };

        /* Send Login Request */
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("doctor");

        } else {

            alert("Invalid credentials!");

        }

    } catch (error) {

        console.error(error);
        alert("Something went wrong. Please try again.");

    }

};
