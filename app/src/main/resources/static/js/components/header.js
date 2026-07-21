/*
 * header.js
 * Handles dynamic rendering of the application header.
 */

/**
 * Renders the page header based on the current user's role.
 */
function renderHeader() {
    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    /* Root Page */
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");

        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="../assets/images/logo/logo.png"
                         alt="Hospital CRM Logo"
                         class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>
        `;
        return;
    }

    /* Session Information */
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    let headerContent = `
        <header class="header">

            <div class="logo-section">
                <img src="../assets/images/logo/logo.png"
                     alt="Hospital CRM Logo"
                     class="logo-img">

                <span class="logo-title">
                    Hospital CMS
                </span>
            </div>

            <nav>
    `;

    /* Validate Session */
    if (
        (role === "loggedPatient" ||
            role === "admin" ||
            role === "doctor") &&
        !token
    ) {
        localStorage.removeItem("userRole");

        alert("Session expired or invalid login. Please log in again.");

        window.location.href = "/";
        return;
    }

    /* Role-Based Navigation */
    if (role === "admin") {

        headerContent += `
            <button
                id="addDocBtn"
                class="adminBtn"
                onclick="openModal('addDoctor')">
                Add Doctor
            </button>

            <a href="#" onclick="logout()">
                Logout
            </a>
        `;

    } else if (role === "doctor") {

        headerContent += `
            <button
                class="adminBtn"
                onclick="selectRole('doctor')">
                Home
            </button>

            <a href="#" onclick="logout()">
                Logout
            </a>
        `;

    } else if (role === "patient") {

        headerContent += `
            <button
                id="patientLogin"
                class="adminBtn">
                Login
            </button>

            <button
                id="patientSignup"
                class="adminBtn">
                Sign Up
            </button>
        `;

    } else if (role === "loggedPatient") {

        headerContent += `
            <button
                id="home"
                class="adminBtn"
                onclick="window.location.href='/pages/loggedPatientDashboard.html'">
                Home
            </button>

            <button
                id="patientAppointments"
                class="adminBtn"
                onclick="window.location.href='/pages/patientAppointments.html'">
                Appointments
            </button>

            <a href="#"
               onclick="logoutPatient()">
               Logout
            </a>
        `;
    }

    /* Close Header */
    headerContent += `
            </nav>
        </header>
    `;

    /* Render Header */
    headerDiv.innerHTML = headerContent;

    /* Attach Event Listeners */
    attachHeaderButtonListeners();
}

/**
 * Attaches listeners to dynamically created buttons.
 */
function attachHeaderButtonListeners() {

    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
        patientLogin.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
        patientSignup.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }
}

/**
 * Logs out an Admin or Doctor.
 */
function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");

    window.location.href = "/";
}

/**
 * Logs out a Patient.
 */
function logoutPatient() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");

    window.location.href = "/pages/index.html";
}

/* Render Header */
renderHeader();
