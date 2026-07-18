This Spring Boot application utilizes Spring Boot MVC and REST controllers. With MVC, the Thymeleaf templates are used for the Admin and Doctor dashboards, while the REST APIs serve all other modules. The application interacts with two databases: MySQL for patient, doctor, appointment and admin data, and MongoDB for prescriptions. All controllers route requests through a common service layer, which delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer.
4. The service layer calls the repositories for database operations.
5. Each repository commmunicates with its respective databases.
6. Data is mapped into Java model classes.
7. The models are used in the response layer.
