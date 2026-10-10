# Expense Tracker

A REST API for tracking personal expenses, built with Spring Boot. Currently in progress as the flagship project of a 60-day Java/Spring Boot Masterclass. This README reflects where it stands today, not the finished product.

## Status: In Progress

Expenses are stored in PostgreSQL (via Neon), and the API is now protected with JWT authentication: users can sign up, log in and call the expense endpoints with a token. Expenses are not yet linked to individual users.

## Tech Stack

- Java 21
- Spring Boot 4 (Web, Data JPA, Security)
- Spring Security with BCrypt password hashing
- JWT (JJWT) for stateless authentication
- PostgreSQL (Neon, cloud-hosted)
- H2 (in-memory, used for the JSON-backed profile and local experimentation)
- Hibernate / JPA
- Lombok
- Jackson (JSON serialization)
- Gradle

## Architecture

Two interchangeable service implementations, selected via Spring profile:
- **`db` profile**: persists to PostgreSQL through `ExpenseRepository` (Spring Data JPA)
- **`json` profile**: reads and writes an in-memory list loaded from `expenses.json`, useful for quick local testing without a database

Standard layered structure: `controller` → `service` → `repository`, with `Expense` and `AppUser` as the JPA entities.

### Authentication

- **Sign-up** stores the username and a BCrypt hash of the password; the plain password is never saved.
- **Login** checks the credentials through Spring's `AuthenticationManager` and returns a JWT valid for 10 hours.
- **Every other request** must carry the token in the `Authorization: Bearer <token>` header. `JwtAuthFilter` validates it and identifies the user for that request.
- Sessions are stateless: the server keeps no session, so each request proves itself with its token.

## Example

An expense might look like:

```json
{
  "date": "2026-09-25",
  "amount": 45.00,
  "category": "Entertainment",
  "account": "Cash",
  "note": "Dinner out with Elçin"
}
```

## Endpoints

`/signup` and `/login` are public. All other endpoints require a valid token.

| Method | Path                                           | Auth     | Description                          |
|--------|------------------------------------------------|----------|--------------------------------------|
| POST   | `/signup`                                      | Public   | Create an account and receive a token |
| POST   | `/login`                                       | Public   | Log in and receive a token           |
| GET    | `/`                                            | Token    | Health check                         |
| GET    | `/expenses/categories`                         | Token    | List all distinct categories         |
| GET    | `/expenses/categories/{day}`                   | Token    | Expenses on a given date             |
| GET    | `/expenses/categories/{category}/month?month=` | Token    | Expenses by category and month       |
| GET    | `/expenses/{id}`                               | Token    | Get a single expense by id           |
| POST   | `/expenses`                                    | Token    | Create a new expense                 |
| PUT    | `/expenses/{id}`                               | Token    | Update an existing expense           |
| DELETE | `/expenses/{id}`                               | Token    | Delete an expense                    |

### Using the API

Sign up (the response includes a token):

```bash
curl -X POST http://localhost:8080/signup \
  -H "Content-Type: application/json" \
  -d '{"fullName": "Ada Lovelace", "username": "ada", "password": "choose-a-password"}'
```

Response:

```json
{ "token": "<jwt>", "message": "success" }
```

Log in later with the same credentials:

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ada", "password": "choose-a-password"}'
```

Call a protected endpoint with the token:

```bash
curl http://localhost:8080/expenses/categories \
  -H "Authorization: Bearer <jwt>"
```

A failed sign-up or login returns status 400 with no token and an error message. A request without a valid token to a protected endpoint returns 403.

## Setup

Credentials are supplied via environment variables (`DB_USERNAME`, `DB_PASSWORD`), never committed. See `application.yml` for the expected connection format.

## Current Limitations

- Expenses are not linked to users, so every authenticated user sees the same expenses.
- All users have the same access; there are no roles yet.
- The JWT signing key is generated at startup, so every token stops working after a restart.
- Failed sign-up and login both return 400, and a missing token returns 403 instead of 401.

## Roadmap

What's still ahead:
- Map each expense to its owner and add role-based access control
- Load the JWT signing key from configuration instead of generating it at startup
- Return precise status codes for authentication failures (409, 401)
- A JavaFX front end (login/signup, expense table, add/edit views, stats view)
- Deployment (Railway) and a packaged JAR