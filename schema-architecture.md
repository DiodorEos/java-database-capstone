# Smart Clinic Management System Architecture

## Section 1: Architecture Summary

The Smart Clinic Management System uses a three-tier architecture: a presentation tier for user interfaces and REST API consumers, an application tier built with Spring Boot, and a data tier containing MySQL and MongoDB. Spring MVC controllers provide server-rendered Admin and Doctor dashboards through Thymeleaf templates. REST controllers serve modules such as appointments, the patient dashboard, and patient records through HTTP requests and JSON responses. Both controller types delegate business operations to a shared service layer, keeping request handling separate from business rules and database access.

The service layer coordinates application workflows and accesses data through dedicated repositories. Spring Data JPA repositories manage relational records in MySQL, where Patient, Doctor, Appointment, and Admin models are represented as JPA entities. Spring Data MongoDB repositories manage Prescription documents in MongoDB, supporting flexible, nested prescription data. This separation gives each component a clear responsibility and allows the MVC and REST interfaces to reuse the same business logic.

## Section 2: Numbered Flow of Data and Control

1. **User Interface Layer:** A user opens an Admin or Doctor dashboard, or interacts with a REST-backed module such as Appointments, Patient Dashboard, or Patient Record. The browser or API client sends an HTTP request to the Spring Boot application.

2. **Controller Layer:** Spring MVC routes the request according to its URL and HTTP method. A Thymeleaf MVC controller handles requests for server-rendered pages, while a REST controller handles API requests. The controller accepts and validates request input, then delegates the requested operation to the service layer.

3. **Service Layer:** The service applies business rules and coordinates the workflow. For example, when booking an appointment, it checks doctor availability before requesting that the appointment be saved. Both MVC and REST controllers use this layer for business operations.

4. **Repository Layer:** The service calls the repository appropriate to the data involved. Spring Data JPA repositories provide access to patients, doctors, appointments, and admins, while Spring Data MongoDB repositories provide access to prescriptions. Repositories encapsulate persistence operations so services do not need to implement database access directly.

5. **Database Access:** The persistence infrastructure reads or writes records in the relevant database. MySQL stores structured relational data and its relationships, while MongoDB stores prescription documents that can contain flexible, nested information. A workflow accesses the database or databases required for that operation.

6. **Model Binding and Persistence Mapping:** Retrieved MySQL records are mapped to Java JPA entity classes annotated with `@Entity`, and MongoDB prescription documents are mapped to Java document classes annotated with `@Document`. Repository results return to the service layer as Java objects, which the service uses to prepare the operation's result for the controller.

7. **Application Models and Response:** For an MVC request, the controller places the resulting data in the view model and selects a Thymeleaf template, which is rendered into HTML and returned to the browser. For a REST request, response objects or DTOs are serialized into JSON and returned to the API client. This completes the request-response cycle.
