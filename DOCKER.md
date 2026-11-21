# Docker deployment

The deployment stack contains:

- `frontend`: Angular production build served by Nginx on port `80`.
- `backend`: Spring Boot application on port `8080`.
- `db`: MySQL 8.4 with a persistent `mysql-data` volume.

## Start

Copy `.env.example` to `.env`, replace the passwords and JWT secret, then run from the repository root:

```bash
docker compose up --build -d
```

Open `http://localhost/`. Nginx serves the Angular SPA and forwards `/api/...` requests to the backend. The first database startup uses Hibernate's `update` mode so an empty MySQL volume can be used for a first deployment. Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` after managing the schema with a migration tool.

To follow logs or stop the stack:

```bash
docker compose logs -f backend
docker compose down
```

The database volume is retained by `docker compose down`. Remove it only when intentionally resetting the database:

```bash
docker compose down -v
```

The sample seed data can be loaded after the application has created the schema:

```bash
docker compose exec -T db mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < language-school/sample-data.sql
```
