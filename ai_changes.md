COMMIT_MESSAGE: Add ProgressTracker entity with member progress tracking and link notifications to progress entries

## Features Added
- **Progress Tracker**: new entity/endpoint set to record and track a member's fitness progress entries (title, description, progress percentage 0-100, status, recorded date). Supports "Track progress" as a create operation plus list/get/update/delete.
- **Add notification**: new `POST /api/v1/notifications` endpoint letting the authenticated user create a notification for themselves (type + message), optionally linking it to a specific Progress Tracker entry via `progressTrackerId`.
- **Relationship**: `Notification` now has an optional (nullable) `progress_tracker_id` FK to `ProgressTracker`, so a notification may be linked to a specific progress tracker entry. Existing notification creation flows (membership activation, payment status) are untouched and continue to create notifications with no progress-tracker link (backward-compatible overload).
- All new list endpoints use offset-based pagination (`page`, `size`, default size 20) consistent with the rest of the API, under the existing `/api/v1` prefix.

## Files Modified
- `src/main/java/com/example/app/entity/Notification.java` — added optional `progressTracker` ManyToOne relationship (`progress_tracker_id` column, nullable).
- `src/main/java/com/example/app/service/NotificationService.java` — added `ProgressTrackerService` dependency, new `create(user, type, message, progressTracker)` overload, and `createForUser(user, NotificationRequest)` used by the new endpoint. Original 3-arg `create` kept for existing callers (delegates with `null` tracker).
- `src/main/java/com/example/app/controller/NotificationController.java` — added `POST /api/v1/notifications` endpoint (`create`), injected `UserService` to resolve the current user entity.
- `src/main/resources/application.properties` — `server.port` changed to `20383`; `jwt.access-expiry-ms` changed to `3600000` (60 minutes) per auth target.
- `src/main/resources/data.sql` — seeded one `progress_trackers` row and linked a second notification example to it; added sequence realignment for `progress_trackers`.
- `docker-compose.yml` — updated `app` service port mapping/env (`SERVER_PORT`) from `26787` to `20383`.
- `Dockerfile` — updated default `SERVER_PORT`/`EXPOSE` from `26787` to `20383`.
- `Makefile` — updated default `SERVER_PORT` fallback for `make run` from `26787` to `20383`.
- `start.sh` — updated default `SERVER_PORT` fallback from `26787` to `20383`.

## Files Added
- `src/main/java/com/example/app/entity/ProgressTracker.java` — new JPA entity (`progress_trackers` table): member link, title, description, progressPercentage, status, recordedAt, createdAt.
- `src/main/java/com/example/app/repository/ProgressTrackerRepository.java` — Spring Data repository with `findByMemberId` pagination query.
- `src/main/java/com/example/app/dto/ProgressTrackerRequest.java` — validated request DTO (memberId, title, progressPercentage 0-100, optional description/status/recordedAt).
- `src/main/java/com/example/app/service/ProgressTrackerService.java` — CRUD + status validation (`NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`) business logic ("Track progress" operation).
- `src/main/java/com/example/app/controller/ProgressTrackerController.java` — REST controller under `/api/v1/progress-trackers` (list all, list by member, get by id, create/"track", update, delete).
- `src/main/java/com/example/app/dto/NotificationRequest.java` — validated request DTO for the new "add notification" endpoint (type, message, optional progressTrackerId).
- `start_40ef5cfdd40ca732.sh` — platform deployment boot script (not committed to git).

## Secrets Moved
- No new hardcoded secrets were found in Java source. `jwt.secret` was already externalized via `@Value("${jwt.secret}")` with an `application.properties` default (`jwt.secret=${JWT_SECRET:...}`); left as-is.

## DB URLs Resolved
- `jdbc:postgresql://localhost:5432/gen_2a2cfb7448c2` -> `jdbc:postgresql://localhost:5432/gen_2a2cfb7448c2` (unchanged — already the working pre-resolved URL; no edit required in `application.properties`).

## Compilation Result
PASSED — `./gradlew compileJava -q` and `./gradlew bootJar -q` both completed with zero errors after all changes.
