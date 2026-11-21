# Sakura Japanese Language School

Sakura Japanese Language School is a full-stack language-school management application with a Spring Boot API and an Angular frontend. It supports public course and batch browsing, student registration and enrollment, JWT authentication, administrator management, and teacher class views.

## Technology stack

- Java 17 and Spring Boot 4
- Spring Data JPA with MySQL 8.4
- JWT-based authentication and Spring Security
- Angular 22 and TypeScript
- Docker Compose with Nginx for the production frontend

## Run with Docker

Docker Compose starts the frontend, backend, and MySQL database together.

```powershell
Copy-Item .env.example .env
# Edit .env and replace the passwords and APP_SECURITY_JWT_SECRET.
docker compose up --build -d
```

Open [http://localhost](http://localhost). Nginx serves the Angular application and proxies `/api/...` requests to Spring Boot. The database is persisted in the `language-school_mysql-data` Docker volume.

If MySQL is already using host port `3306`, set this in `.env` before starting:

```env
MYSQL_PORT=3307
```

The container still uses MySQL port `3306` internally; only the host-side port changes.

Useful commands:

```powershell
docker compose ps
docker compose logs -f backend
docker compose down
```

Do not use `docker compose down -v` unless you intentionally want to delete the Docker database volume.

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

## Security

Do not commit `.env` or production credentials. Set `APP_SECURITY_JWT_SECRET` to a randomly generated value of at least 32 UTF-8 bytes in every deployed environment. The committed configuration contains only a local-development placeholder.

## License

This project is licensed under the [MIT License](LICENSE).
