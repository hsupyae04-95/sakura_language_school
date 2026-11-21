# Sakura Japanese Language School — JWT Authentication Specification

## 1. Purpose

This specification defines an incremental implementation plan for JWT authentication in the existing **Sakura Japanese Language School** Spring Boot application.

The implementation must use standard servlet-based Spring MVC and Spring Security. It must not introduce WebFlux, reactive controllers, reactive repositories, `Mono`, `Flux`, or other reactive authentication components.

This is a plan and acceptance contract. No implementation step should begin until the preceding step has been reviewed and approved.

## 2. Current project baseline

The Maven project root is the directory containing `pom.xml`:

```text
D:\language_school\language-school
```

Current package root:

```text
com.sakura.languageschool
```

Relevant existing components inspected:

```text
model/
  Users.java
  Roles.java
  UserRole.java
  UserRoleId.java

repository/
  UserDao.java
  RoleDao.java
  UserRoleDao.java

security/
  SecurityUtil.java
  webConfig.java

service/
  AuthService.java
  impl/AuthServiceImpl.java

bean/Mapper.java
model/dto/AuthRequest.java
controller/AuthController.java
```

Existing behavior and constraints observed:

- `Users` stores `username`, `email`, `password`, and `enabled`.
- User roles are represented through the `Users` → `UserRole` → `Roles` relationship and the embedded key `UserRoleId`.
- `UserDao` already supports lookup by username and email.
- `UserRoleDao` already exposes a user-role lookup and role-existence query.
- `SecurityUtil` already delegates password hashing to a Spring `PasswordEncoder` and must remain the password-hashing boundary.
- `AuthServiceImpl` already hashes passwords, saves users, resolves a role, and saves the user-role relationship during account creation.
- `Mapper` already owns ModelMapper configuration and should be reused for DTO conversion where suitable.
- `webConfig` currently disables CSRF and permits every request.
- `AuthController` currently exposes `/api/auth/register`, but its current registration path delegates to `StudentService` rather than directly to `AuthServiceImpl`.
- `pom.xml` includes `spring-boot-starter-webmvc`, Spring Data JPA, Spring Security, MySQL, ModelMapper, and an existing `spring-boot-starter-webflux` dependency. WebFlux is pre-existing and must not be used by the JWT implementation; its removal or retention is a separate, explicitly approved dependency decision.
- `application.properties` currently contains database settings but no JWT secret or token-lifetime settings.
- Existing tests include Spring context and DAO/service tests, but there is no JWT authentication test suite yet.

No existing entity, repository, or service should be replaced merely to introduce JWT authentication.

## 3. Goals

### Required

1. Authenticate users with a username and password using Spring Security’s servlet-based authentication flow.
2. Issue a signed JWT after successful authentication.
3. Read `Authorization: Bearer <token>` on subsequent servlet requests.
4. Validate signature and token time claims before establishing an authenticated `SecurityContext`.
5. Derive authorities from the existing `Roles` and `UserRole` data.
6. Keep password hashing in the existing `SecurityUtil`/`PasswordEncoder` boundary.
7. Reuse `Users`, `Roles`, `UserRole`, `UserRoleId`, `UserDao`, `RoleDao`, `UserRoleDao`, `AuthServiceImpl`, and `Mapper` where their existing responsibilities apply.
8. Use constructor injection for all new or modified Spring-managed classes.
9. Preserve the current package structure under `com.sakura.languageschool`.
10. Implement and review the work in small approval-gated steps.

### Explicitly out of scope for the first JWT implementation

- WebFlux or reactive security.
- OAuth2 authorization server functionality.
- Refresh tokens, token revocation, logout blacklists, or persistent sessions.
- Social login or external identity providers.
- Replacing the existing data model with a many-to-many mapping.
- Unrelated controller, service, or database refactoring.
- Returning passwords or password hashes in any response.

## 4. Proposed authentication contract

### Login request

Add a dedicated DTO under the existing DTO package, for example:

```text
com.sakura.languageschool.model.dto.LoginRequest
```

Expected JSON:

```json
{
  "username": "student1",
  "password": "student-password"
}
```

The DTO must validate that both fields are present. Authentication failures must not reveal whether the username or password was the incorrect part.

### Login response

Add a dedicated response DTO under the existing DTO package, for example:

```text
com.sakura.languageschool.model.dto.AuthResponse
```

Initial response shape:

```json
{
  "accessToken": "<signed-jwt>",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

The exact `expiresIn` unit must be documented in the DTO and tests. The response must not expose `Users.password`.

### Endpoints

Keep the existing registration route:

```text
POST /api/auth/register
```

Add:

```text
POST /api/auth/login
```

The registration and login endpoints must be public. All other application endpoints should require authentication by default once the JWT security chain is enabled. Role restrictions should be added only where the existing business rules clearly require them and should not be invented as part of the first JWT step.

### Request processing

```text
POST /api/auth/login
  -> AuthenticationManager
  -> servlet UserDetailsService backed by UserDao/UserRoleDao
  -> PasswordEncoder/BCrypt verification
  -> JWT service signs token
  -> AuthResponse

Request with Authorization: Bearer <token>
  -> OncePerRequestFilter
  -> JWT signature/claim validation
  -> authenticated principal + authorities
  -> controller
```

## 5. Design decisions

### Servlet-only security

Use the servlet APIs and Spring Security types appropriate for Spring MVC, including:

- `SecurityFilterChain`
- `AuthenticationManager`
- `UserDetailsService`
- `OncePerRequestFilter`
- `UsernamePasswordAuthenticationToken`
- `SecurityContext`

Do not use `ServerHttpSecurity`, `ReactiveAuthenticationManager`, `ReactiveUserDetailsService`, `WebFilter`, `Mono`, or `Flux`.

Step 1 selects JJWT `0.13.0` for the servlet-compatible JWT foundation. It uses the JJWT API at compile time and the implementation/Jackson integration at runtime. Token creation and validation remain intentionally deferred to Step 2.

### Existing user and role model

The application’s existing entities remain the source of truth. A user-details adapter may be introduced in the security package, but it must read from `UserDao` and obtain roles through the existing user-role relationship/DAO.

Authority conversion must be consistent and tested. If database role values are stored as `ADMIN`, `TEACHER`, or `STUDENT`, the Spring authorities should use one documented convention, preferably `ROLE_ADMIN`, `ROLE_TEACHER`, and `ROLE_STUDENT`. Do not silently create or mutate role records during login.

Before relying on `UserRoleDao.findByUserId`, verify that the derived Spring Data query matches the current embedded-id mapping. If it does not, make the smallest repository query correction necessary and cover it with a repository test.

### Password handling

`SecurityUtil` remains responsible for password hashing used during registration. Authentication must use the same Spring `PasswordEncoder` bean for password verification through the normal Spring Security authentication flow. Plain-text passwords must never be logged, stored, placed in a JWT, or returned in a response.

### JWT contents

The first implementation should use only the claims required for authentication:

- `sub`: stable user identifier or username, with the chosen meaning documented and tested.
- `iat`: issued-at time.
- `exp`: expiration time.
- optional role/authority claim only if the implementation needs it; authorities must not be trusted without a valid signature.

Do not place email, password, or mutable profile data in the token. The signing key must come from configuration, not source code.

### Secret configuration

Add configuration properties for the JWT signing secret and access-token lifetime. The secret must be supplied through an environment-specific configuration mechanism and must not be committed as a real secret.

For local development, a clearly marked placeholder may be documented, but tests should use a test-only secret. Startup validation should reject a missing or unusably short production signing secret once configuration binding is implemented.

## 6. Incremental implementation plan and approval gates

Each step ends with a review pause. The next step requires explicit user approval.

### Step 0 — Baseline verification (inspection only)

Tasks:

- Confirm the Maven project root and package structure.
- Run the existing test/compile checks if the local MySQL/database requirements allow it.
- Record any pre-existing failures before modifying code.
- Verify the current behavior of `AuthController`, `AuthServiceImpl`, `SecurityUtil`, and `webConfig`.
- Verify how roles are stored and whether the existing `UserRoleDao` query is valid for the embedded ID mapping.
- Decide, explicitly, whether the existing WebFlux dependency should be removed as cleanup. This decision is not required to write JWT code, but the implementation must not depend on it.

Exit criteria:

- A baseline result is reported.
- No application code is changed.

**Approval gate:** stop and request approval for Step 1.

### Step 1 — Dependency and configuration foundation

Tasks:

- Select one JWT library compatible with Java 17 and servlet-based Spring Security, or use Spring Security’s servlet-compatible JWT support if the project’s dependency set provides all required pieces.
- Add only the required Maven dependencies.
- Do not add reactive dependencies for JWT.
- Add typed JWT configuration for signing secret and token lifetime under the existing package structure.
- Keep secrets out of source control.
- If approved separately, remove the pre-existing WebFlux starter and verify the application still starts as a servlet application.

Exit criteria:

- `mvn test` or the project’s equivalent verification command passes, or all failures are clearly identified as pre-existing/environmental.
- Configuration binding is covered by a focused test.
- No JWT is issued yet.

**Approval gate:** stop and request approval for Step 2.

### Step 2 — JWT service and DTOs

Tasks:

- Add `LoginRequest` and `AuthResponse` under `model.dto`.
- Add a focused JWT service under `security` to create and validate access tokens.
- Implement signature validation, `iat`, and `exp` handling.
- Define behavior for malformed, expired, unsupported, or incorrectly signed tokens.
- Keep the service independent of controllers and avoid coupling it to WebFlux.

Exit criteria:

- Unit tests cover token creation, valid validation, expiration, malformed input, and signature failure.
- Tests prove that sensitive fields are absent from the token.
- No request filter or login endpoint is enabled yet.

**Approval gate:** stop and request approval for Step 3.

### Step 3 — UserDetails loading from existing persistence

Tasks:

- Add a servlet `UserDetailsService` implementation under `security`.
- Load users through `UserDao.findByUsername`.
- Reject disabled users using the existing `Users.enabled` field.
- Load roles through the existing `UserRole`/`Roles` model and `UserRoleDao` as appropriate.
- Convert roles to the documented authorities convention.
- Use constructor injection and final dependencies.
- Make only the smallest DAO/query change needed if the existing embedded-id query cannot load roles correctly.

Exit criteria:

- Unit/repository tests cover a valid user, unknown user, disabled user, and role mapping.
- Existing registration behavior is preserved.
- No JWT filter is enabled yet.

**Approval gate:** stop and request approval for Step 4.

### Step 4 — Login authentication service and endpoint

Tasks:

- Expose `AuthenticationManager` from the servlet security configuration.
- Add a login method to the existing authentication service boundary, or introduce the smallest supporting service while preserving `AuthService` and `AuthServiceImpl` responsibilities.
- Authenticate with `UsernamePasswordAuthenticationToken`.
- Issue an access token only after successful authentication.
- Add `POST /api/auth/login` to the existing auth controller/package.
- Reuse `ApiUtil`/existing response conventions where appropriate.
- Return an appropriate unauthorized response without leaking authentication details.
- Refactor touched classes from field injection to constructor injection without broad unrelated rewrites.

Exit criteria:

- Mock MVC/integration tests cover successful login, wrong password, unknown user, disabled user, validation errors, and response shape.
- Registration still hashes passwords and creates the requested user-role relationship.
- The login endpoint is public, but bearer-token request authentication is not enabled yet if that is the safest sequencing choice.

**Approval gate:** stop and request approval for Step 5.

### Step 5 — Bearer-token request filter and security rules

Tasks:

- Add a servlet `OncePerRequestFilter` under `security`.
- Read only the Bearer authorization scheme.
- Validate the token using the JWT service.
- Load the user/principal and set an authenticated `SecurityContext` only after validation succeeds.
- Continue the chain for missing or invalid tokens so Spring Security can produce the configured unauthorized response.
- Register the filter before `UsernamePasswordAuthenticationFilter`.
- Configure stateless sessions.
- Permit `/api/auth/register` and `/api/auth/login`; require authentication for other API routes by default.
- Keep CSRF behavior appropriate for the stateless API and document the decision.
- Configure consistent `401 Unauthorized` and `403 Forbidden` behavior.

Exit criteria:

- End-to-end tests cover public routes, valid bearer access, missing token, expired token, malformed token, bad signature, and insufficient authority.
- The application remains servlet-based and no reactive class is imported.
- Existing controllers still start and the default security behavior is intentional.

**Approval gate:** stop and request approval for Step 6.

### Step 6 — Role authorization and hardening

Tasks:

- Add endpoint-level role rules only from confirmed business requirements.
- Use `hasRole`/`hasAuthority` consistently with the documented role prefix convention.
- Review CORS, error responses, logging, and sensitive-data handling.
- Confirm JWT lifetime and signing-key operational guidance.
- Add regression tests for registration, login, existing endpoints, and role boundaries.
- Review whether WebFlux can be removed safely if that was approved in Step 1.

Exit criteria:

- Full test suite passes in a configured test environment.
- Security behavior is documented and reproducible.
- No unrelated refactoring is included.

**Approval gate:** final review and acceptance.

## 7. File/package impact forecast

Likely additions, subject to approval and final naming:

```text
src/main/java/com/sakura/languageschool/security/
  JwtService.java
  JwtAuthenticationFilter.java
  CustomUserDetailsService.java
  JwtProperties.java                 # if typed configuration is used

src/main/java/com/sakura/languageschool/model/dto/
  LoginRequest.java
  AuthResponse.java
```

Likely small modifications:

```text
pom.xml
src/main/resources/application.properties
src/main/java/com/sakura/languageschool/security/webConfig.java
src/main/java/com/sakura/languageschool/security/SecurityUtil.java
src/main/java/com/sakura/languageschool/service/AuthService.java
src/main/java/com/sakura/languageschool/service/impl/AuthServiceImpl.java
src/main/java/com/sakura/languageschool/controller/AuthController.java
```

No replacement of the existing entity model is planned.

## 8. Constructor injection standard

All new Spring-managed classes must use constructor injection:

```java
private final UserDao userDao;
private final UserRoleDao userRoleDao;

public CustomUserDetailsService(UserDao userDao, UserRoleDao userRoleDao) {
    this.userDao = userDao;
    this.userRoleDao = userRoleDao;
}
```

When an existing class is touched for authentication, convert only its involved dependencies to constructor injection if this can be done without unrelated churn. Do not combine the JWT work with a broad project-wide injection refactor.

## 9. Testing strategy

Tests must remain servlet-based.

Required coverage over the staged implementation:

- JWT unit tests: issue, validate, expire, malformed token, invalid signature.
- User-details tests: lookup, disabled account, missing account, role mapping.
- Login tests: valid credentials, invalid credentials, validation failure, no password leakage.
- Filter/security tests: public routes, valid bearer token, missing token, invalid token, stateless behavior.
- Authorization tests: confirmed role-protected route behavior.
- Regression tests: existing registration, password hashing, and user-role persistence.

Do not add WebTestClient or reactive test fixtures. Use servlet-compatible MockMvc and ordinary Spring Security test support where appropriate.

## 10. Security acceptance criteria

The implementation is accepted only when all of the following are true:

- Passwords are BCrypt-hashed and never returned or logged.
- JWTs are signed, time-limited, and validated before authentication.
- The signing secret is configuration-driven and not hard-coded.
- Authentication is stateless.
- Missing, malformed, expired, and incorrectly signed tokens cannot authenticate a request.
- Disabled users cannot authenticate.
- Authorities come from the existing role data and follow one tested naming convention.
- Registration and login are public; protected routes require a valid token.
- The implementation uses standard servlet Spring Security only.
- No new reactive classes, reactive security configuration, or reactive endpoints are introduced.
- Every staged step was stopped for user approval before the next step began.

## 11. Approval protocol

The implementation assistant must report the result of the current step, including changed files and verification results, then stop. The assistant must not proceed to the next numbered step until the user explicitly approves it.

Suggested approval phrases:

```text
Approve Step 1
Approve Step 2
Approve Step 3
Approve Step 4
Approve Step 5
Approve Step 6
```

## 12. Sample-data checkpoint before Step 3

Before starting Step 3, the repeatable seed script at `sample-data.sql` was executed against the configured local `language_school` MySQL database.

The seed is isolated under `seed_` usernames/emails and `Seed` course/batch names. It does not update or delete existing rows and can be rerun without creating duplicates.

Seeded records:

- 1 admin account, 8 teacher accounts, and 40 student accounts.
- 8 teacher profiles and 40 student profiles.
- 12 courses and 24 batches.
- 24 enrollments with `APPROVED`, `PENDING`, and `REJECTED` sample states.

All seeded accounts use the local demo password `SakuraDemo123!`. This is sample data only and must not be reused in a real environment.

The live database currently has unique constraints on both `enrollment.student_id` and `enrollment.batch_id`, so the seed creates one enrollment per seeded batch/student pair. This differs from the entity-level composite uniqueness declaration and should be reviewed separately if multiple enrollments per student or batch are required.

Step 3 remains pending explicit approval.

## 13. Step 3 implementation checkpoint

Step 3 was implemented after approval.

- Added `security.CustomUserDetailsService` using constructor injection.
- User lookup uses `UserDao.findByUsername`.
- Disabled users are rejected with Spring Security's `DisabledException`.
- Authorities are loaded through `UserRoleDao.findByUserId` and normalized to the `ROLE_*` convention.
- The existing `UserRoleDao.findByUserId` query was changed to a targeted join-fetch query so the lazy `Roles` association is initialized before the security service maps authorities.
- Added unit tests for enabled, unknown, disabled, duplicate-prefix, and role-authority behavior.
- Added a repository integration test against the seeded database for the embedded `UserRoleId` mapping.

Step 4 remains pending explicit approval. No login endpoint or bearer-token filter was added in Step 3.

## 14. Step 4 implementation checkpoint

Step 4 was implemented after approval.

- Exposed the servlet `AuthenticationManager` through `AuthenticationConfiguration`.
- Added `AuthService.login(LoginRequest)` and implemented credential authentication with `UsernamePasswordAuthenticationToken`.
- Issued a signed access token through the existing `JwtService` only after successful authentication.
- Added `POST /api/auth/login` while preserving the existing registration route.
- Returned the existing API success envelope for login responses.
- Returned a generic `401 Unauthorized` response for wrong credentials, unknown users, and disabled users.
- Refactored the touched authentication service and controller to constructor injection.
- Added unit, MockMvc, and real database-backed login integration tests.

Step 5 remains pending explicit approval. The bearer-token filter and protected-route security rules were not added in Step 4.

## 15. Step 5 implementation checkpoint

Step 5 was implemented after approval.

- Added the servlet `JwtAuthenticationFilter` based on `OncePerRequestFilter`.
- Valid bearer tokens now load the user through `CustomUserDetailsService` and populate the servlet `SecurityContext` with role authorities.
- Missing, malformed, expired, incorrectly signed, unknown-user, and disabled-user tokens cannot authenticate protected requests.
- Configured stateless session creation and registered the JWT filter before `UsernamePasswordAuthenticationFilter`.
- Only `/api/auth/register` and `/api/auth/login` are public; other routes require authentication.
- Added the existing administrative enrollment-review route as `ROLE_ADMIN`-only based on its controller business rule.
- Configured generic servlet `401 Unauthorized` and `403 Forbidden` handlers.
- Added MockMvc integration coverage for valid bearer access, missing/invalid tokens, expiration, bad signatures, and insufficient authority.

Step 6 remains pending explicit approval. Broader role authorization and hardening review were not started.

## 16. Step 6 implementation checkpoint

Step 6 was implemented after approval.

Role rules now enforced at the servlet security boundary:

- `ADMIN`: the explicitly listed `/api/admin/users`, `/api/admin/courses`, `/api/admin/batches`, `/api/admin/teachers`, and `/api/admin/enrollments` routes.
- `TEACHER`: `GET /api/teacher/batches` and `GET /api/teacher/batches/{id}/students`.
- `STUDENT`: `POST /api/enrollments` and `GET /api/enrollments/my`.
- Public: registration, login, active course catalog reads, and available batch reads.
- Requests outside the documented contract are denied by the security chain.

Hardening completed:

- Security failures now return JSON API errors with `UNAUTHORIZED` or `FORBIDDEN` codes instead of container error pages.
- CORS is explicitly disabled by default; a deliberate production allowlist should be added when a separate frontend origin is introduced.
- Default SQL logging was disabled to reduce operational log exposure.
- JWT signing remains environment-driven through `JWT_SECRET`, requires at least 32 UTF-8 bytes, and uses a one-hour access-token lifetime.
- JWTs contain no password, email, or profile data; roles are reloaded from persistence on each bearer-authenticated request.
- Added the Spring Boot 4 `spring-boot-starter-jackson` dependency because JSON serialization uses the Boot 4 `tools.jackson` API.
- The pre-existing WebFlux dependency was not removed because removal was never separately approved, and no reactive authentication code is used.

Final verification before the endpoint contract update: `mvnw.cmd test` passed with 33 tests and 0 failures.

## 17. MVP endpoint and authorization contract

The following endpoint matrix supersedes the earlier broad “authenticated by default” route description. The paths below are the supported MVP API contract. All other API paths are denied by the servlet security chain.

### Authentication and users

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Student registration | Public |
| POST | `/api/auth/login` | Login and receive JWT | Public |
| GET | `/api/admin/users` | Get all users | `ADMIN` |
| GET | `/api/admin/users/{id}` | Get user by ID | `ADMIN` |
| PUT | `/api/admin/users/{id}` | Update user | `ADMIN` |
| PATCH | `/api/admin/users/{id}/deactivate` | Deactivate user | `ADMIN` |

### Courses and batches

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| GET | `/api/courses` | Browse active courses | Public |
| GET | `/api/courses/{id}` | View course details | Public |
| POST | `/api/admin/courses` | Create course | `ADMIN` |
| PUT | `/api/admin/courses/{id}` | Update course | `ADMIN` |
| PATCH | `/api/admin/courses/{id}/deactivate` | Deactivate course | `ADMIN` |
| GET | `/api/batches` | Browse available batches | Public |
| GET | `/api/batches/{id}` | View batch details | Public |
| POST | `/api/admin/batches` | Create batch | `ADMIN` |
| PUT | `/api/admin/batches/{id}` | Update batch | `ADMIN` |

### Teachers

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| POST | `/api/admin/teachers` | Create teacher account and profile | `ADMIN` |
| GET | `/api/admin/teachers` | Get all teachers | `ADMIN` |
| GET | `/api/admin/teachers/{id}` | Get teacher details | `ADMIN` |
| PUT | `/api/admin/teachers/{id}` | Update teacher | `ADMIN` |
| GET | `/api/teacher/batches` | View assigned batches | `TEACHER` |
| GET | `/api/teacher/batches/{id}/students` | View approved students | `TEACHER` |

### Enrollments

| Method | Endpoint | Purpose | Access |
|---|---|---|---|
| POST | `/api/enrollments` | Apply for a batch | `STUDENT` |
| GET | `/api/enrollments/my` | View own enrollments | `STUDENT` |
| GET | `/api/admin/enrollments` | View enrollment applications | `ADMIN` |
| GET | `/api/admin/enrollments/{id}` | View application details | `ADMIN` |
| PATCH | `/api/admin/enrollments/{id}/approve` | Approve application | `ADMIN` |
| PATCH | `/api/admin/enrollments/{id}/reject` | Reject application | `ADMIN` |

### Endpoint implementation notes

- Public course and batch controllers expose read-only catalog operations; admin controllers expose write operations under the `/api/admin` prefix.
- The authenticated username is used to resolve the student profile for `POST /api/enrollments` and `GET /api/enrollments/my`; the client cannot submit another student's identity for these operations.
- Approval and rejection record the authenticated admin as reviewer. The optional request body may contain `adminNote`; the endpoint determines the status from its path.
- Teacher batch access is restricted in the service layer to batches assigned to the authenticated teacher. Student results contain approved enrollments only.
- Existing service methods and entities remain reused. Legacy controller mappings that are not part of this matrix are not authorized by the security chain.
- The endpoint security tests were updated for the public catalog, exact admin paths, student restrictions, and the teacher/student route boundaries.

Final verification after the endpoint contract update: `mvnw.cmd test` passed with 34 tests and 0 failures.

