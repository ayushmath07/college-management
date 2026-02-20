# College Management Portal

A REST API for managing college operations - student enrollment, attendance, marks, fees, and announcements. Built with Spring Boot and PostgreSQL.

## Tech Stack

- Java 21, Spring Boot 4.0.1
- Spring Data JPA + PostgreSQL
- Spring Security + JWT (jjwt 0.12.3)
- Springdoc OpenAPI (Swagger UI)
- Maven, Lombok

## Prerequisites

- JDK 21
- Maven 3.8+
- PostgreSQL 14+

## Setup

1. Clone the repo and create a PostgreSQL database:

```sql
CREATE DATABASE college_management;
```

2. Update `src/main/resources/application.properties` with your DB credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/college_management
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

3. Run:

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Project Structure

```
src/main/java/com/landminesoft/CollegeManagement/
├── config/          # Security, OpenAPI, PasswordEncoder configs
├── controller/      # REST controllers (Auth, Student, Faculty, Admin, etc.)
├── dto/             # Request/response DTOs
├── entity/          # JPA entities (Student, Faculty, Course, etc.)
├── exception/       # Custom exceptions + global handler
├── repository/      # Spring Data JPA repos
├── security/        # JWT filter, utils, UserDetailsService
└── service/         # Business logic
```

## API Endpoints

### Auth (`/api/auth`)

- `POST /student/register` - Register student
- `POST /student/login` - Student login → returns JWT
- `POST /faculty/register`, `POST /faculty/login`
- `POST /admin/register`, `POST /admin/login`
- `POST /forgot-password` - Request reset token
- `POST /reset-password` - Reset with token
- `POST /change-password` - Change password (needs JWT)

### Student (`/api/student`) - needs STUDENT role

- `GET/PUT /profile`
- `POST /enroll`, `GET /enrollments`, `PUT /enrollments/{id}/drop`
- `GET /attendance`, `GET /marks`
- `GET /fees`, `POST /fees/pay`

### Faculty (`/api/faculty`) - needs FACULTY role

- `GET/PUT /profile`
- `GET /courses`, `GET /courses/{id}/students`
- `POST /attendance` - Mark attendance
- `POST /marks`, `GET /courses/{id}/marks`

### Admin (`/api/admin`) - needs ADMIN role

- `POST /subjects`, `GET /subjects`, `GET /subjects/filter`
- `POST /courses`, `GET /courses`
- `POST /fees/structures`, `GET /fees/structures`, `GET /fees/pending`
- `POST /announcements`, `GET /announcements`
- `GET /dashboard`

### Public

- `GET /api/announcements` - Active announcements
- `GET /api/announcements/audience/{audience}`

## Database

11 tables: `student`, `faculty_personal`, `admin`, `subject`, `course`, `enrollment`, `attendance`, `marks`, `fee_structure`, `fee_payment`, `announcement`

Schema is auto-managed by Hibernate (`ddl-auto=update`).

## Testing

```bash
mvn test
```

Currently has 86 unit tests using Mockito covering all service classes + JWT utils.

## Environment Variables

For production deployment, set these:

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `JWT_SECRET` (min 64 chars), `JWT_EXPIRATION` (ms, default 86400000)
- `PORT` (default 8080)

See `application-prod.properties` for details.

## Known Issues

- `generateRollNumber()` in AuthService can produce duplicates under concurrent registrations - needs a DB sequence
- Forgot password prints the reset token to console instead of sending email (no SMTP configured yet)
- No pagination on list endpoints - could be slow with large datasets

## License

This project is developed as part of an internship assignment.
