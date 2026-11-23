# Sakura Japanese Language School

Sakura Japanese Language School is a full-stack language-school management application with a Spring Boot API and an Angular frontend. In this application, students can explore available Japanese language courses and submit enrollment requests. Administrators can manage courses, teachers, and enrollment requests. Teachers can access their assigned class information.

## Project Features

### Student
- Register and log in securely.
- Browse available courses and class batches.
- Submit enrollment requests.
- View enrollment status.

### Teacher
- Log in to the teacher dashboard.
- View assigned classes.
- Access class and student information.

### Adminstrator
- Manage courses and class batches.
- Manage teacher accounts.
- Review and approve student enrollment requests.
- Manage school information.

## Application Workflow
- Students create an account and log in.
- Students browse available courses and class batches.
- Students submit an enrollment request.
- Administrators review and approve or reject enrollment requests.
- Teachers access their assigned classes and student information.

## Technology Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- JWT Authentication
- REST API

### Frontend
- Angular
- TypeScript

### Development and Deployment

- Docker and Docker Compose
- Nginx
- Maven
- Git and GitHub

## Architecture
This application uses a layered backend architecture:

- Controller: Handles HTTP requests and responses.
- Service: Contains business logic.
- Repository: Handles database operations.
- Entity: Represents database tables.
- DTO: Separates API request and response data.
The Angular frontend communicates with the Spring Boot backend through REST APIs. MySQL stores application data.

## Run with Docker

Docker Compose starts the frontend, backend, and MySQL database together.

```powershell
Copy-Item .env.example .env
# Edit .env and replace the passwords and APP_SECURITY_JWT_SECRET.
docker compose up --build -d
```

Open [http://localhost](http://localhost). Nginx serves the Angular application and proxies `/api/...` requests to Spring Boot. The database is persisted in the `language-school_mysql-data` Docker volume.

## Migrate an existing MySQL database

To copy an existing local `language_school` database into Docker, start the stack first and then run `mysqldump` and `mysql` from a machine with the MySQL client tools installed. The following commands prompt for passwords instead of committing them to the shell history:

```powershell
mysqldump -h 127.0.0.1 -P 3306 -u root -p `
  --single-transaction --routines --triggers --events --hex-blob --no-tablespaces `
  language_school |
mysql -h 127.0.0.1 -P 3307 -u language_school -p language_school
```

Adjust the destination port and username to match `.env`. Back up important data before migrating. The Docker database uses Hibernate `update` mode by default for a first deployment; switch `SPRING_JPA_HIBERNATE_DDL_AUTO` to `validate` after adopting a schema migration process.

## Local development

### Backend

The backend requires Java 17 and a running MySQL database. Configure the datasource and JWT secret through environment variables, then run:

```powershell
Set-Location language-school
./mvnw.cmd spring-boot:run
```

### Frontend

The frontend requires Node.js 22 and npm:

```powershell
Set-Location frontend
npm ci
npm start
```

Open [http://localhost:4200](http://localhost:4200). The Angular development proxy forwards API requests to the backend.

## Verification

```powershell
Set-Location frontend
npm run build
npm test -- --watch=false

Set-Location ..\language-school
./mvnw.cmd test
```

## Repository layout

```text
language-school/       Spring Boot backend
frontend/              Angular frontend
docker/backend/        Backend production image
docker/frontend/       Frontend image and Nginx proxy
docker-compose.yml     Frontend, backend, and MySQL services
```


## License

This project is licensed under the [MIT License](LICENSE).
