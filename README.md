# Expense Tracker

A REST API for tracking personal expenses, built with Spring Boot. It is the flagship project of a 60-day Java/Spring Boot Masterclass and is still in progress: this README describes where it stands today, not the finished product.

## Status: In Progress

Expenses are stored in PostgreSQL (Neon). Users sign up, log in and manage their own expenses with a JWT. Every expense belongs to the user who created it, and there are two roles: USER and ADMIN.

## Tech Stack

- Java 21
- Spring Boot 4 (Web, Data JPA, Security)
- Spring Security with BCrypt password hashing
- JWT (JJWT) for stateless authentication
- PostgreSQL (Neon, cloud-hosted), Hibernate / JPA
- Lombok, Jackson
- Gradle

## Architecture

Layered structure: `controller` → `service` → `repository`, with `Expense` and `AppUser` as the JPA entities. Service interfaces live in `service` and their implementations in `service.impl`. An expense belongs to one user (many-to-one), and every service method takes the id of the logged-in user, so users can only read, change or delete their own expenses.

Entities are not returned where they hold sensitive data: the admin user list uses a DTO (`UserListDTO`) that leaves out the password hash. Errors are handled in one place: services throw exceptions and `GlobalExceptionHandler` turns them into HTTP responses.

### Authentication and Roles

- **Sign-up** stores the username and a BCrypt hash of the password. The plain password is never saved. New accounts get the USER role.
- **Login** checks the credentials through Spring's `AuthenticationManager` and returns a JWT valid for 10 hours.
- **Every other request** carries the token in the `Authorization: Bearer <token>` header. `JwtAuthFilter` validates it and identifies the user for that request. Sessions are stateless: the server keeps nothing between requests.
- **The signing key** is read from configuration (`JWT_SECRET`), so tokens stay valid across restarts.
- **USER** adds, edits and deletes their own expenses. Another user's expense is never returned: asking for it gives 404. Any `id` sent when creating an expense is ignored, so one user can never overwrite another's expense.
- **ADMIN** can list users through `/admin/users` but cannot use the expense endpoints. A single admin account is created at startup by `DataInitializer`.

## Endpoints

`/signup` and `/login` are public. The expense endpoints need a USER token and work on the logged-in user's expenses only. `/admin/**` needs an ADMIN token.

| Method | Path                                           | Access | Description                                           |
|--------|------------------------------------------------|--------|-------------------------------------------------------|
| POST   | `/signup`                                      | Public | Create an account and receive a token (201)           |
| POST   | `/login`                                       | Public | Log in and receive a token (200)                      |
| GET    | `/`                                            | USER   | Health check                                          |
| GET    | `/expenses/categories`                         | USER   | List your distinct categories                         |
| GET    | `/expenses/categories/{day}`                   | USER   | Your expenses on a given date                         |
| GET    | `/expenses/categories/{category}/month?month=` | USER   | Your expenses by category and month                   |
| GET    | `/expenses/{id}`                               | USER   | Get one of your expenses by id                        |
| POST   | `/expenses`                                    | USER   | Create a new expense (the id is assigned by the server) |
| PUT    | `/expenses/{id}`                               | USER   | Update one of your expenses                           |
| DELETE | `/expenses/{id}`                               | USER   | Delete one of your expenses                           |
| GET    | `/admin/users`                                 | ADMIN  | List all users (id, full name, username, role)        |

### Using the API

Sign up (the response includes a token):

```bash
curl -X POST http://localhost:8080/signup \
  -H "Content-Type: application/json" \
  -d '{"fullName": "Elcin Del Core", "username": "elcDc", "password": "elcDC"}'
```

Response (201 Created):

```json
{ "token": "<jwt>" }
```

Log in later with the same credentials (200 OK, same response body):

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "elcDc", "password": "elcDC"}'
```

Create an expense with the token:

```bash
curl -X POST http://localhost:8080/expenses \
  -H "Authorization: Bearer <jwt>" \
  -H "Content-Type: application/json" \
  -d '{"date": "2026-09-25", "amount": 45.00, "category": "Entertainment", "account": "Cash", "note": "Dinner out with Elçin"}'
```

Failed requests never return a token:

- Signing up with a username that already exists returns **409 Conflict** with an error message.
- Logging in with a wrong username or password returns **401 Unauthorized**.
- A request to a protected endpoint with a missing or invalid token returns **401 Unauthorized**.
- A request with a valid token but the wrong role (for example an admin calling an expense endpoint) returns **403 Forbidden**.

## Setup

Configuration is supplied through environment variables and never committed. See `application.yml` for the expected format.

| Variable         | Required | Purpose                                                        |
|------------------|----------|----------------------------------------------------------------|
| `DB_URL`         | Yes      | JDBC address of the PostgreSQL database                        |
| `DB_USERNAME`    | Yes      | Database user                                                  |
| `DB_PASSWORD`    | Yes      | Database password                                              |
| `ADMIN_PASSWORD` | No       | Password of the `admin` account (default for local use only)  |
| `JWT_SECRET`     | No       | Text used to sign tokens, at least 32 characters (local default if unset) |
| `PORT`           | No       | Port the server listens on (default 8080)                      |

On first start, `DataInitializer` creates an `admin` account with the password from `ADMIN_PASSWORD`. If the variable is not set, a development default is used. The local defaults for `ADMIN_PASSWORD` and `JWT_SECRET` are public, so set your own values on any real deployment.

## Current Limitations

- No input validation yet.
- No automated tests yet.
- The admin account is only created if none exists, so changing `ADMIN_PASSWORD` later does not change an existing admin.

## Roadmap

- Input validation
- Tests (JUnit, MockMvc)
- Deployment (Railway) with its own database, and a packaged JAR
- A Thymeleaf front end (login/signup, expense table, add/edit views, stats view)