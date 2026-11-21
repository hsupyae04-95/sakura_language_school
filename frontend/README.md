# Sakura Japanese Language School — Frontend

Angular 22 standalone frontend for the Sakura Japanese Language School MVP. The first screen is a responsive public course catalog inspired by the supplied reference layout and redesigned around a Sakura pink, warm white, and soft ink palette.

## Run locally

```bash
npm install
npm start
```

Open `http://localhost:4200/`.

## Verify

```bash
npm run build
npm test -- --watch=false
```

The frontend now connects to the Spring Boot API through the development proxy and includes:

- Live public courses and upcoming batches.
- Student registration, login, JWT session restoration, batch applications, and application status.
- Admin enrollment review plus course/user overview.
- Admin creation of courses, batches, and teacher accounts, plus course/user deactivation.
- Teacher assigned-batch and approved-student views.
- API loading, empty, error, and offline fallback states.
- Student enrolled-class schedules after approval and an explicit “No completed courses yet” state.
- Admin account activation/deactivation for student and teacher accounts.
- Batch capacity tracking with unavailable/full states and blocked applications when capacity is reached.

The development proxy forwards `/api` to `http://localhost:8080`. Start the Spring Boot backend before using live data:

```bash
cd ..
./mvnw spring-boot:run
```

The frontend uses these Spring Boot endpoints:

- `GET /api/courses`
- `GET /api/batches`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/enrollments/my`
- `POST /api/enrollments`
- `GET /api/admin/enrollments`
- `PATCH /api/admin/enrollments/{id}/approve|reject`
- `GET /api/teacher/batches`
- `GET /api/teacher/batches/{id}/students`

## Test accounts and end-to-end flow

Run the repeatable seed script from the project root before testing:

```bash
cd ..
mysql -u root -p language_school < sample-data.sql
cd frontend
```

All three test accounts use the password `SakuraDemo123!`:

| Role | Username | What to test |
|---|---|---|
| Admin | `seed_admin` | Create courses, batches, and teachers; review applications; activate or deactivate accounts |
| Teacher | `seed_teacher01` | View assigned classes and approved student lists |
| Student | `seed_student01` | Browse batches, submit an application, and view approved schedules |

Suggested flow:

1. Sign in as `seed_student01`, browse an open batch, and submit an application. The dashboard shows it as `PENDING` and shows `No completed courses yet` until a completed batch is approved.
2. Sign in as `seed_admin`, open Enrollment applications, and approve the student application. The admin dashboard also provides course, batch, teacher, and account management.
3. Sign in again as `seed_student01`. The approved batch now appears under My enrolled classes with its start date, end date, and schedule.
4. Sign in as `seed_teacher01` to open an assigned batch and view its approved students.

To test a pre-seeded completed course, use `seed_student10` with the same password; the seed data gives that account an approved completed batch.

Icons are implemented as a small local inline SVG component in `src/app/icon.ts`, keeping the app offline-safe and avoiding a version-mismatched icon dependency.
