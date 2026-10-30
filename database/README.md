# Module 3: Sample Data and Stored Procedures

These files reproduce the IBM labs using MySQL and MongoDB. Use an isolated training database: all people, appointments, prescriptions, and credentials in the fixtures are synthetic. Do not load these records into a real clinic database.

## Files

| File | Purpose |
| --- | --- |
| `01-create-database.sql` | Create `cms`; Hibernate creates its tables on application startup. |
| `02-sample-data.sql` | Insert the lab's 25 doctors, 100 availability entries, 25 patients, 130 appointments, and one admin. |
| `prescriptions.json` | The lab's 24 prescription documents, in MongoDB Extended JSON. |
| `03-prescriptions.mongosh.js` | Load prescriptions without duplicating existing document IDs; add an appointment lookup index. |
| `04-stored-procedures.sql` | Create the three named reporting procedures. |
| `05-verification.sql` | Execute all required SQL verification queries and report calls. |

## Configuration and execution

Use JDK 17, MySQL 8.4, MongoDB 8.0, and MongoDB Shell. The Maven wrapper downloads Maven when first used. Start the database instances using your lab environment and obtain their connection information. The application reads these environment variables:

| Variable | Meaning |
| --- | --- |
| `MYSQL_URL` | JDBC URL for `cms`, including the host, port, and appropriate TLS options. |
| `MYSQL_USER` | Database account authorized for `cms`. |
| `MYSQL_PASSWORD` | That account's password. |
| `MONGODB_URI` | Authenticated URI including the `prescriptions` database and correct `authSource`. |
| `JWT_SECRET` | Private random signing secret for the later authentication lab. |
| `JPA_DDL_AUTO` | Defaults to `update` for the table-generation lab. Use deliberate migrations for production. |

Set credentials in your current process environment or an external secret store, never in committed properties or URLs pasted into Git history. Spring Boot does not automatically load a repository `.env` file. The default JDBC URL requests identity-verified TLS; configure the trust material for your deployment. The isolated local verification uses encrypted MySQL transport (`sslMode=REQUIRED`) with the server's local certificate; that is not identity verification for a remote deployment.

1. In the MySQL CLI, source `database/01-create-database.sql` as an authorized provisioning account.
2. Start the application from `app` using `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). Supply the environment variables above. Hibernate creates `admin`, `appointment`, `doctor`, `doctor_available_times`, and `patient`.
3. In the MySQL CLI, inspect `SHOW TABLES` and ensure all five tables are empty with fresh auto-increment counters. Then source `database/02-sample-data.sql` once. The lab's relationship IDs assume a fresh database. Do not rerun this seed on populated tables, reset existing records, or use `--force` to ignore SQL errors.
4. From the repository root, connect with `mongosh` using your lab credentials and run `load('database/03-prescriptions.mongosh.js')`. The script reads `database/prescriptions.json` relative to the current working directory. Its fixed IDs and `$setOnInsert` allow safe reruns without overwriting existing prescriptions.
5. In MySQL, source `database/04-stored-procedures.sql`. It replaces only the three named routines. It does not delete tables or data.
6. Capture the required verification output using the CLI's `tee` command, source `database/05-verification.sql`, and finish with `notee`. In MongoDB, capture `use prescriptions;` followed by `db.prescriptions.find().limit(5).pretty();`.

Paths supplied to `SOURCE` are relative to the MySQL client's working directory. Use absolute paths when needed. The application account needs schema-creation rights during this lab; a later deployment should separate migration and runtime permissions. The routines use `SQL SECURITY INVOKER`, so callers need permission to execute the routines and read the underlying tables.

## Required report calls

```sql
CALL GetDailyAppointmentReportByDoctor('2025-04-15');
CALL GetDoctorWithMostPatientsByMonth(4, 2025);
CALL GetDoctorWithMostPatientsByYear(2025);
```

The daily procedure returns appointment detail rows ordered by doctor and time; it is not a grouped aggregate. The monthly and yearly procedures deliberately retain the lab's `COUNT(patient_id)` semantics: repeat visits count separately, and status is not filtered. Despite the output label `patients_seen`, these are appointment counts, not unique patients or completed consultations. Equal counts resolve deterministically to the lowest doctor ID. Invalid parameters are rejected; an empty reporting period returns no rows.

## Compatibility and security decisions

- Explicit model mappings now use the lab's singular table names and `doctor_available_times.available_times`, so its SQL works directly. Existing plural-named tables from an earlier design are not automatically migrated; this lab was prepared for a fresh database.
- Hyphens were removed from sample phone numbers to satisfy the required ten-digit model validation and column width.
- Sample passwords are stored as bcrypt hashes with cost 12, not plaintext. The later login service must use bcrypt verification, not string equality. Known course passwords remain public demo credentials even when hashed and must never be reused in a deployed system.
- Appointment IDs, dates, and status values remain those provided by the lab. The 2025 dates are historical fixtures for reproducible reports, not future bookings. Direct SQL insertion bypasses Java `@Future` validation. Some source bookings do not fit the declared daily availability and some overlap; they are retained for lab/report fidelity, not presented as valid input for the future booking service.
- The prescription references use BSON Int64 to match Java Long. A positive ID does not prove a relationship: validation must check the referenced appointment and patient in MySQL. MongoDB has no cross-store foreign key.
- The properties file contains no database password or embedded JWT signing secret. It also corrects the starter's malformed MongoDB URI and Thymeleaf boolean setting.

## Submission

Submit the committed `app/src/main/resources/application.properties` and the actual outputs of the three report calls. The detailed sample-data lab also asks for the complete table-query and MongoDB-query output. Execution evidence is stored in `evidence/module-3` after a successful run. SQL scripts are included so the results can be reproduced; scripts alone do not replace the required execution output.
