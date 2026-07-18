# Admin User Stories

**1. Log Into Portal:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. The admin can enter a valid username and password to log in.
2. The system grants access to the admin dashboard upon successful authentication.
3. The system displays an error message if the username or password is incorrect.

**Priority:** High
**Story Points:** 3
**Notes:**
- Password should be securely encrypted.
- Consider account lockout after multiple failed login attempts.

**2. Log Out of Portal:**
_As an admin, I want to log out of the portal, so that I can protect system access after finishing my session._

**Acceptance Criteria:**
1. The admin can log out by selecting the logout option.
2. The system ends the current session immediately.
3. The admin is redirected to the login page after logging out.

**Priority:** High
**Story Points:** 1
**Notes:**
- Expire the session token after logout.
- Prevent access to protected pages using the browser's back button.

**3. Add Doctors to Portal:**
_As an admin, I want to add doctors to the portal, so that new doctors can access and be managed within the system._

**Acceptance Criteria:**
1. The admin can enter a doctor's required information (e.g., name, email, specialty).
2. The system validates that all required fields are completed before saving.
3. The system confirms that the doctor's profile has been successfully created.

**Priority:** High
**Story Points:** 5
**Notes:**
- Prevent duplicate doctor accounts using the same email.
- Required fields should be clearly indicated.

**4. Delete Doctor's Profile from Portal:**
_As an admin, I want to delete a doctor's profile from the portal, so that inactive or incorrect accounts can be removed._

**Acceptance Criteria:**
1. The admin can select a doctor's profile for deletion.
2. The system asks for confirmation before permanently deleting the profile.
3. The doctor's profile is removed from the portal after confirmation.

**Priority:** High
**Story Points:** 3
**Notes:**
- Only admins should have permission to delete doctor profiles.
- Consider logging deletion activity for auditing purposes.

**5. View List of Doctors:**
_As an admin, I want to view the list of doctors in the portal, so that I can manage existing doctor profiles._

**Acceptance Criteria:**
1. The system displays a list of all registered doctors.
2. The admin can locate a doctor by browsing or searching the list.
3. The displayed information includes key details such as the doctor's name, specialty, and contact information.

**Priority:** Medium
**Story Points:** 2
**Notes:**
- The doctor list should refresh after a doctor is added or deleted.
- Consider pagination if the number of doctors becomes large.

# Patient User Stories

**1. View List of Doctors:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._

**Acceptance Criteria:**
1. Visitors can access the list of doctors without creating an account or logging in.
2. The system displays basic information for each doctor (e.g., name, specialty, and availability).
3. Visitors are prompted to sign up or log in when attempting to book an appointment.

**Priority:** Medium  
**Story Points:** 2  

**Notes:**
- Only public doctor information should be displayed.
- Sensitive doctor or patient information should remain hidden.

**2. Sign Up for Appointments:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. The patient can create an account by providing a valid email address and password.
2. The system validates that the email is unique and the password meets security requirements.
3. The system confirms successful registration and allows the patient to log in.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Passwords should be securely encrypted.
- Email verification can be implemented as a future enhancement.

**3. Log Into Portal:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. The patient can log in using a valid email and password.
2. The system grants access to the patient's dashboard after successful authentication.
3. The system displays an error message if the login credentials are invalid.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Consider locking the account after multiple failed login attempts.
- Sessions should be securely managed.

**4. Log Out of Portal:**  
_As a patient, I want to log out of the portal, so that I can secure my account after using the system._

**Acceptance Criteria:**
1. The patient can log out by selecting the logout option.
2. The system immediately ends the current session.
3. The patient is redirected to the login page after logging out.

**Priority:** High  
**Story Points:** 1  

**Notes:**
- Expire the session token after logout.
- Prevent access to authenticated pages after logging out.

**5. Book Appointment:**  
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. The patient must be logged in before booking an appointment.
2. The patient can select a doctor, an available date, and a one-hour time slot.
3. The system confirms the booking and prevents double-booking of the selected time slot.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Only available one-hour appointment slots should be displayed.
- Patients should receive a confirmation after successfully booking an appointment.
