# API Test Results

All endpoints tested against `http://localhost:26787` with inline curl commands.
This reflects the FINAL state after the fix iterations in Step 15 (see notes column
for bugs found and fixed during the run).

## Auth

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/auth/register | valid new user | PASSED | 201, returns token/refreshToken |
| POST | /api/v1/auth/register | duplicate email | PASSED | 400 as expected |
| POST | /api/v1/auth/register | invalid email format | PASSED | 400 as expected |
| POST | /api/v1/auth/login | valid credentials | PASSED | 200, token returned |
| POST | /api/v1/auth/login | wrong password | PASSED | 401 as expected |
| POST | /api/v1/auth/refresh | valid refresh token | PASSED | 200, new access token issued |
| POST | /api/v1/auth/logout | revokes refresh token | PASSED | 204 |
| POST | /api/v1/auth/refresh | after logout (revoked jti) | PASSED | 401 as expected (revocation enforced) |

## Users

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| GET | /api/v1/users | as SUPER_ADMIN | PASSED | 200 |
| GET | /api/v1/users | as MEMBER | PASSED | 403 as expected (admin only) |
| GET | /api/v1/users | no token | PASSED | 401 as expected |
| GET | /api/v1/users/{id} | self | PASSED | 200, password field not exposed |
| GET | /api/v1/users/{id} | another user's record | PASSED | 403 as expected |
| PUT | /api/v1/users/{id} | self, non-role field | PASSED | 200 |
| PUT | /api/v1/users/{id} | self attempts role change | PASSED | 403 as expected (role escalation blocked) |
| DELETE | /api/v1/users/{id} | admin deletes test user | PASSED | 204 |

## Gyms

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/gyms | valid, as GYM_ADMIN/SUPER_ADMIN | PASSED | 201 |
| POST | /api/v1/gyms | missing name | PASSED | 400 as expected |
| POST | /api/v1/gyms | as MEMBER | PASSED | 403 as expected |
| GET | /api/v1/gyms | paginated list | PASSED | 200 |
| GET | /api/v1/gyms/{id} | existing | PASSED | 200, body matches created gym |
| PUT | /api/v1/gyms/{id} | update fields | PASSED | 200, updated body verified |
| DELETE | /api/v1/gyms/{id} | cascade delete (branches/etc.) | PASSED | 204 |
| GET | /api/v1/gyms/{id} | after delete | PASSED | 404 as expected |

## Branches

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/branches | valid | PASSED | 201 |
| POST | /api/v1/branches | missing gymId | PASSED | 400 as expected |
| POST | /api/v1/branches | invalid gymId | PASSED | 404 as expected |
| GET | /api/v1/branches | paginated list | PASSED | 200 (fixed: was 500, see Notes below) |
| GET | /api/v1/branches/{id} | existing | PASSED | 200, nested gym object correct |
| PUT | /api/v1/branches/{id} | update | PASSED | 200 |
| DELETE | /api/v1/branches/{id} | cascade delete | PASSED | 204 |
| GET | /api/v1/branches/{id} | after delete | PASSED | 404 as expected |

## Members

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/members | valid, staff role | PASSED | 201 |
| POST | /api/v1/members | missing email | PASSED | 400 as expected |
| GET | /api/v1/members | staff role | PASSED | 200 |
| GET | /api/v1/members | as MEMBER role (not staff) | PASSED | 403 as expected |
| GET | /api/v1/members/{id} | staff | PASSED | 200 |
| PUT | /api/v1/members/{id} | valid status update | PASSED | 200, status field verified |
| PUT | /api/v1/members/{id} | invalid status enum | PASSED | 400 as expected |
| DELETE | /api/v1/members/{id} | cascade delete (memberships/etc.) | PASSED | 204 |
| GET | /api/v1/members/{id} | after delete | PASSED | 404 as expected |

## Trainers

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/trainers | valid | PASSED | 201 |
| POST | /api/v1/trainers | invalid userId | PASSED | 404 as expected |
| GET | /api/v1/trainers | list | PASSED | 200 |
| GET | /api/v1/trainers/{id} | existing | PASSED | 200 |
| PUT | /api/v1/trainers/{id} | update | PASSED | 200 |
| DELETE | /api/v1/trainers/{id} | cascade delete (classes/workouts) | PASSED | 204 |
| GET | /api/v1/trainers/{id} | after delete | PASSED | 404 as expected |

## Memberships (lifecycle)

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/memberships | purchase, valid | PASSED | 201, status=PENDING |
| POST | /api/v1/memberships | negative price | PASSED | 400 as expected |
| PUT | /api/v1/memberships/{id}/activate | pending -> active | PASSED | 200, startDate/endDate calculated |
| PUT | /api/v1/memberships/{id}/activate | already active | PASSED | 400 as expected |
| PUT | /api/v1/memberships/{id}/renew | active -> extended | PASSED | 200, endDate extended |
| PUT | /api/v1/memberships/{id}/cancel | -> cancelled | PASSED | 200, status=CANCELLED |
| GET | /api/v1/memberships/{id} | existing | PASSED | 200 |
| GET | /api/v1/memberships | list | PASSED | 200 |
| DELETE | /api/v1/memberships/{id} | delete | PASSED | 204 |
| GET | /api/v1/memberships/{id} | after delete | PASSED | 404 as expected |

## Workouts

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/workouts | valid | PASSED | 201 |
| POST | /api/v1/workouts | invalid trainerId | PASSED | 404 as expected |
| GET | /api/v1/workouts | list | PASSED | 200 |
| GET | /api/v1/workouts/{id} | existing | PASSED | 200 |
| PUT | /api/v1/workouts/{id} | valid status | PASSED | 200, status=COMPLETED verified |
| PUT | /api/v1/workouts/{id} | invalid status | PASSED | 400 as expected |
| DELETE | /api/v1/workouts/{id} | delete | PASSED | 204 |
| GET | /api/v1/workouts/{id} | after delete | PASSED | 404 as expected |

## Fitness Classes

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/classes | valid | PASSED | 201 |
| POST | /api/v1/classes | invalid capacity (0) | PASSED | 400 as expected |
| GET | /api/v1/classes | list | PASSED | 200 |
| GET | /api/v1/classes/{id} | existing | PASSED | 200 |
| POST | /api/v1/classes/{id}/register | valid, within capacity | PASSED | 201, registeredCount incremented to 1 |
| POST | /api/v1/classes/{id}/register | over capacity | PASSED | 400 as expected |
| POST | /api/v1/classes/{id}/register | duplicate active registration | PASSED | 400 as expected |
| DELETE | /api/v1/classes/{id}/register | cancel registration | PASSED | 204, registeredCount decremented to 0 |
| PUT | /api/v1/classes/{id} | update | PASSED | 200 |
| DELETE | /api/v1/classes/{id} | cascade delete (registrations) | PASSED | 204 (fixed: was 500 FK violation, see Notes) |
| GET | /api/v1/classes/{id} | after delete | PASSED | 404 as expected |

## Appointments

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/appointments | valid | PASSED | 201 |
| POST | /api/v1/appointments | overlapping trainer slot | PASSED | 400 as expected (conflict prevention) |
| POST | /api/v1/appointments | non-overlapping slot | PASSED | 201 |
| GET | /api/v1/appointments | list | PASSED | 200 |
| GET | /api/v1/appointments/{id} | existing | PASSED | 200 |
| PUT | /api/v1/appointments/{id}/status | valid status | PASSED | 200 |
| PUT | /api/v1/appointments/{id}/status | invalid status | PASSED | 400 as expected |
| DELETE | /api/v1/appointments/{id} | delete | PASSED | 204 |
| GET | /api/v1/appointments/{id} | after delete | PASSED | 404 as expected |

## Attendance

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/attendance/checkin | valid | PASSED | 201 |
| POST | /api/v1/attendance/checkin | missing memberId | PASSED | 400 as expected |
| PUT | /api/v1/attendance/{id}/checkout | valid | PASSED | 200, checkOutTime set |
| PUT | /api/v1/attendance/{id}/checkout | already checked out | PASSED | 400 as expected |
| GET | /api/v1/attendance | list | PASSED | 200 |
| GET | /api/v1/attendance/{id} | existing | PASSED | 200 |
| DELETE | /api/v1/attendance/{id} | delete | PASSED | 204 |
| GET | /api/v1/attendance/{id} | after delete | PASSED | 404 as expected |

## Payments

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| POST | /api/v1/payments | valid | PASSED | 201, status=PENDING |
| POST | /api/v1/payments | negative amount | PASSED | 400 as expected |
| PUT | /api/v1/payments/{id}/status | staff, SUCCESS -> notification created | PASSED | 200, status=SUCCESS verified |
| PUT | /api/v1/payments/{id}/status | invalid status | PASSED | 400 as expected |
| PUT | /api/v1/payments/{id}/status | as MEMBER (not staff) | PASSED | 403 as expected |
| GET | /api/v1/payments | list | PASSED | 200 |
| DELETE | /api/v1/payments/{id} | delete | PASSED | 204 |
| GET | /api/v1/payments/{id} | after delete | PASSED | 404 as expected |

## Notifications

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| GET | /api/v1/notifications | own list (seed notification) | PASSED | 200, contains seeded MEMBERSHIP_ACTIVATED item |
| GET | /api/v1/notifications/{id} | owner access | PASSED | 200 |
| GET | /api/v1/notifications/{id} | another member's notification | PASSED | 403 as expected |
| PUT | /api/v1/notifications/{id}/read | owner marks read | PASSED | 200, read=true verified |

## Infra / Docs / Health

| Method | Endpoint | Case | Result | Notes |
|---|---|---|---|---|
| GET | /actuator/health | health check | PASSED | 200 {"status":"UP"} |
| GET | /docs | swagger UI | PASSED | 302 redirect to swagger-ui/index.html (expected) |
| GET | /api-docs | OpenAPI JSON | PASSED | 200 |
| GET | /ws/info | SockJS handshake info | PASSED | 200 |
| OPTIONS | /api/v1/gyms | CORS preflight | PASSED | 200, allow-origin echoed |

## Bugs found and fixed during Step 15 iterative loop

1. **401 vs 403 on missing token** — Spring Security defaulted unauthenticated
   requests to 403 (anonymous + AccessDeniedException). Fixed by adding an
   `AuthenticationEntryPoint` (`HttpStatusEntryPoint(UNAUTHORIZED)`) to the
   `SecurityFilterChain`'s `exceptionHandling`.
2. **500 on any GET returning lazy `@ManyToOne` associations** — Jackson could
   not serialize Hibernate proxy classes ("Type definition error:
   ByteBuddyInterceptor"). Fixed by adding `jackson-datatype-hibernate6` and
   registering a `Hibernate6Module` bean with `FORCE_LAZY_LOADING=true`.
2b. **Password hash leaking in nested JSON** (e.g. `trainer.user.password`) —
   Added `@JsonIgnore` to `User.password`.
3. **500 on DELETE for Gym/Branch/Trainer/Member/FitnessClass with history** —
   Postgres foreign-key violations (`23503`) surfaced as generic 500s. Fixed by
   (a) mapping `DataIntegrityViolationException` to a 409 response in the
   global exception handler, and (b) adding a `CascadeDeleteService` that
   deep-deletes dependent registrations/workouts/appointments/attendance/
   payments/memberships before removing a parent Member/Trainer/Branch/Gym/
   FitnessClass, and detaches `Payment.membership` before a Membership delete.

All endpoints listed above are PASSED after these fixes were applied and
retested. No endpoints remain FAILED or SKIPPED.
