# Job Application Tracker API

A production-ready REST API for tracking job applications through their full lifecycle — from initial application to offer or rejection.

Built with **Java 17**, **Spring Boot 3.2**, **Spring Data JPA**, **PostgreSQL**, and **Spring Security**.

---

## Features

- Full CRUD for job applications
- Forward-only status state machine with validation
- Filter by status, date range, or both
- Full-text search across company and role
- Summary counts by status
- Stateless HTTP Basic Auth (BCrypt)
- Global exception handling with structured error responses
- 27 automated tests (unit + integration) using H2 in-memory DB

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL (prod) / H2 (test) |
| Security | Spring Security — stateless Basic Auth |
| Build | Maven |
| Testing | JUnit 5 + MockMvc |
| Deployment | Render (Docker) |

---

## API Endpoints

Base URL: `/api/applications`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/applications` | List all applications (supports `?status=`, `?from=`, `?to=`) |
| `GET` | `/api/applications/search?query=` | Full-text search on company and role |
| `GET` | `/api/applications/summary` | Count of applications grouped by status |
| `GET` | `/api/applications/{id}` | Get a single application |
| `POST` | `/api/applications` | Create a new application |
| `PATCH` | `/api/applications/{id}` | Update application fields |
| `PATCH` | `/api/applications/{id}/status` | Transition application status |
| `DELETE` | `/api/applications/{id}` | Delete an application |

All endpoints require Basic Auth: `admin` / `password`.

---

## Status State Machine

```
APPLIED ──► SCREENING ──► INTERVIEW ──► OFFER
   │            │              │           │
   └────────────┴──────────────┴───────────┴──► WITHDRAWN
   │            │              │
   └────────────┴──────────────┴──────────────► REJECTED
```

- Transitions are **forward-only** — you cannot go backwards
- `WITHDRAWN` is a universal exit reachable from any state
- `REJECTED` and `WITHDRAWN` are terminal — no further transitions allowed
- Invalid transitions return `409 Conflict`

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL running on `localhost:5432`

### Setup

```bash
# Clone the repo
git clone https://github.com/71ashu/job-tracker-api.git
cd job-tracker-api

# Create the database
psql -U postgres -c "CREATE DATABASE jobtracker;"

# Run the app
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Run Tests

```bash
mvn test
```

Tests use an H2 in-memory database — no PostgreSQL required.

---

## Example Requests

### Create an application
```bash
curl -u admin:password -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Acme Corp",
    "role": "Software Engineer",
    "status": "APPLIED",
    "dateApplied": "2024-01-15",
    "location": "Remote",
    "jobUrl": "https://acme.com/jobs/123"
  }'
```

### Filter by status
```bash
curl -u admin:password "http://localhost:8080/api/applications?status=INTERVIEW"
```

### Transition status
```bash
curl -u admin:password -X PATCH http://localhost:8080/api/applications/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SCREENING"}'
```

### Search
```bash
curl -u admin:password "http://localhost:8080/api/applications/search?query=engineer"
```

### Get summary
```bash
curl -u admin:password http://localhost:8080/api/applications/summary
```

---

## Project Structure

```
src/
├── main/java/com/jobtracker/api/
│   ├── JobTrackerApiApplication.java
│   ├── config/
│   │   └── DataSourceConfig.java       # Handles Render's postgres:// URL format
│   ├── controller/
│   │   └── JobApplicationController.java
│   ├── dto/
│   │   ├── CreateApplicationRequest.java
│   │   ├── UpdateApplicationRequest.java
│   │   └── StatusTransitionRequest.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InvalidStatusTransitionException.java
│   │   └── ResourceNotFoundException.java
│   ├── model/
│   │   ├── ApplicationStatus.java      # Enum with state machine logic
│   │   └── JobApplication.java         # JPA entity
│   ├── repository/
│   │   └── JobApplicationRepository.java
│   ├── security/
│   │   └── SecurityConfig.java
│   └── service/
│       └── JobApplicationService.java
└── test/java/com/jobtracker/api/
    ├── ApplicationStatusTest.java       # 19 state machine unit tests
    └── JobApplicationIntegrationTest.java # 8 MockMvc integration tests
```

---

## Deployment

This project is deployed on **Render** using Docker. The `render.yaml` blueprint provisions both the web service and a PostgreSQL database automatically.

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/71ashu/job-tracker-api)

---

## License

MIT
