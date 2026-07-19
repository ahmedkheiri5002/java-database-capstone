## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- date_of_birth: DATE, Not Null
- gender: VARCHAR(20), Not Null
- phone: VARCHAR(15), Not Null
- email: VARCHAR(100), Unique
- address: VARCHAR(255)
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- specialization: VARCHAR(100), Not Null
- phone: VARCHAR(15), Not Null
- email: VARCHAR(100), Unique, Not Null
- clinic_location_id: INT, Foreign Key → clinic_locations(id)
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
- reason_for_visit: VARCHAR(255)
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Unique, Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "rating": 5,
  "title": "Excellent Experience",
  "comment": "The doctor was very professional and explained everything clearly.",
  "tags": [
    "friendly",
    "professional",
    "quick-service"
  ],
  "metadata": {
    "submittedAt": "2026-07-19T14:30:00Z",
    "device": "Web Browser",
    "anonymous": false
  },
  "doctorResponse": {
    "responded": true,
    "responseDate": "2026-07-20T09:15:00Z",
    "message": "Thank you for your feedback! We're glad we could help."
  }
}
```

### Design Considerations

- The document stores `patientId`, `doctorId`, and `appointmentId` instead of the full patient or doctor object to avoid duplicating data and to keep information consistent with the MySQL database.
- Arrays (such as `tags`) make it easy to categorize and search feedback.
- Embedded documents (`metadata` and `doctorResponse`) group related information together and reduce the need for additional collections.
- MongoDB's flexible schema allows new fields (such as attachments, reaction counts, or moderation status) to be added later without affecting existing documents.
- This design supports future changes while keeping frequently accessed feedback information in a single document.
