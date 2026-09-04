# Gym Management System — Spring Boot Backend

A complete Java 21 / Spring Boot 3 / Gradle backend for managing gyms, branches, trainers,
members, memberships, workouts, fitness classes, personal training appointments,
attendance, payments/billing, and notifications — secured with JWT (access + refresh)
and role-based access control (RBAC).

## Tech Stack

- Java 21, Spring Boot 3.3.4, Gradle 8.10
- Spring Web, Spring Data JPA, Spring Security, Bean Validation
- PostgreSQL
- JWT (`io.jsonwebtoken` / jjwt) — HS256, 30 min access token, 7 day refresh token
- springdoc-openapi (Swagger UI)
- WebSocket / STOMP (real-time notification broadcast)

## Roles

`SUPER_ADMIN`, `GYM_ADMIN`, `BRANCH_MANAGER`, `TRAINER`, `RECEPTIONIST`, `MEMBER`

## Project Structure

```
src/main/java/com/example/app/
  ├── AppApplication.java
  ├── config/        SecurityConfig, CorsConfig, WebSocketConfig
  ├── entity/        JPA entities
  ├── repository/    Spring Data JPA repositories
  ├── service/       Business logic
  ├── controller/    REST controllers (api/v1)
  ├── dto/           Request/response DTOs
  ├── security/      JwtUtil, JwtFilter, UserDetailsServiceImpl, SecurityUtils
  └── exception/     Global exception handling
src/main/resources/
  ├── application.properties
  └── data.sql
```

## Running Locally

Requires a reachable PostgreSQL instance (see `application.properties` for connection
settings — defaults to `jdbc:postgresql://localhost:5432/gen_2a2cfb7448c2`).

```bash
chmod +x gradlew start.sh
./gradlew bootJar -q
bash start.sh          # starts on port 26787 (override with SERVER_PORT env var)
```

Or directly:

```bash
./gradlew bootRun
```

Swagger UI: http://localhost:26787/docs
OpenAPI JSON: http://localhost:26787/api-docs
Health check: http://localhost:26787/actuator/health

## Running with Docker Compose

```bash
docker compose up --build
```

This starts a PostgreSQL container and the application container, wired together.
The app is reachable at http://localhost:26787.

## Makefile Shortcuts

```bash
make build       # compile
make test        # run tests
make package     # build the jar
make run         # package + start.sh
make docker-up   # docker compose up --build -d
make docker-down # docker compose down
```

## Environment Variables

| Variable      | Default (application.properties)                                             | Purpose                        |
|---------------|--------------------------------------------------------------------------------|---------------------------------|
| SERVER_PORT   | 26787                                                                          | HTTP port (start.sh / start.bat)|
| JWT_SECRET    | baked-in default (base64)                                                     | HS256 signing key               |

## Authentication

- `POST /api/v1/auth/register` — create an account (`role` optional, defaults to `MEMBER`)
- `POST /api/v1/auth/login` — returns `{ token, refreshToken, userId, email, role }`
- `POST /api/v1/auth/refresh` — exchange a valid, non-revoked refresh token for a new access token
- `POST /api/v1/auth/logout` — revokes the given refresh token

Send the access token as `Authorization: Bearer <token>` on all other endpoints.

## API Overview (prefix `/api/v1`, pagination via `?page=0&size=20`)

| Resource            | Endpoints                                                                                  |
|---------------------|----------------------------------------------------------------------------------------------|
| Auth                | `POST /auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`                        |
| Users               | `GET /users`, `GET/PUT/DELETE /users/{id}`                                                   |
| Gyms                | `GET /gyms`, `GET/POST/PUT/DELETE /gyms{,/{id}}`                                              |
| Branches            | `GET /branches`, `GET/POST/PUT/DELETE /branches{,/{id}}`                                     |
| Members             | `GET /members`, `GET/POST/PUT/DELETE /members{,/{id}}`                                       |
| Trainers            | `GET /trainers`, `GET/POST/PUT/DELETE /trainers{,/{id}}`                                     |
| Memberships         | `GET/POST /memberships`, `GET/DELETE /memberships/{id}`, `PUT /{id}/activate,/renew,/cancel` |
| Workouts            | `GET /workouts`, `GET/POST/PUT/DELETE /workouts{,/{id}}`                                     |
| Fitness Classes     | `GET /classes`, `GET/POST/PUT/DELETE /classes{,/{id}}`, `POST/DELETE /classes/{id}/register`  |
| Appointments        | `GET /appointments`, `GET/POST/PUT/DELETE /appointments{,/{id}}`, `PUT /{id}/status`         |
| Attendance          | `GET /attendance`, `POST /attendance/checkin`, `PUT /attendance/{id}/checkout`, `DELETE`      |
| Payments            | `GET/POST /payments`, `GET/DELETE /payments/{id}`, `PUT /payments/{id}/status`               |
| Notifications       | `GET /notifications`, `GET/PUT/DELETE /notifications/{id}`                                   |

RBAC: management endpoints (create/update/delete Gyms, Branches, Trainers, delete Members)
are restricted to admin roles (`SUPER_ADMIN`, `GYM_ADMIN`, `BRANCH_MANAGER`); self-scoped
resources (own member profile, own notifications) are open to the owning authenticated user.

## WebSocket

STOMP endpoint at `/ws` (SockJS). Notification events are broadcast to
`/topic/notifications/{userId}` whenever a notification is created (membership activation,
payment success/failure, etc).

## Tests

Curl-based API tests were run against every endpoint; see `/api_tests/test_results.md`
for the full pass/fail breakdown, and `/api_test_report.xlsx` for the structured report.

## Seed Data

`data.sql` inserts 6-8 users (one per role), 2 gyms, 3 branches, 3 members, 2 trainers,
3 memberships, 2 workouts, 2 fitness classes, a class registration, an appointment,
an attendance record, a payment, and a notification. All seeded users share the
password `Test@123`.
