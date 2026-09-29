# Expense Tracker

A REST API for tracking personal expenses, built with Spring Boot. Currently in progress as the flagship project of a 60-day Java/Spring Boot Masterclass — this README reflects where it stands today, not the finished product.

## Status: In Progress

Currently on **Day 45** — integrating PostgreSQL (via Neon) for real persistence, replacing the earlier JSON-file-backed prototype.

## Tech Stack

- Java 21
- Spring Boot 4 (Web, Data JPA)
- PostgreSQL (Neon, cloud-hosted)
- H2 (in-memory, used for the JSON-backed profile and local experimentation)
- Hibernate / JPA
- Lombok
- Jackson (JSON serialization)
- Gradle

## Architecture

Two interchangeable service implementations, selected via Spring profile:
- **`db` profile** — persists to PostgreSQL through `ExpenseRepository` (Spring Data JPA)
- **`json` profile** — reads/writes an in-memory list loaded from `expenses.json`, useful for quick local testing without a database

Standard layered structure: `controller` → `service` → `repository`, with `Expense` as the JPA entity/model.

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

| Method | Path                                         | Description                          |
|--------|-----------------------------------------------|---------------------------------------|
| GET    | `/`                                            | Health check                          |
| GET    | `/expenses/categories`                         | List all distinct categories          |
| GET    | `/expenses/categories/{day}`                   | Expenses on a given date              |
| GET    | `/expenses/categories/{category}/month?month=` | Expenses by category and month        |
| GET    | `/expenses/{id}`                               | Get a single expense by id            |
| POST   | `/expenses`                                    | Create a new expense                  |
| PUT    | `/expenses/{id}`                               | Update an existing expense            |
| DELETE | `/expenses/{id}`                               | Delete an expense                     |

## Setup

Credentials are supplied via environment variables (`DB_USERNAME`, `DB_PASSWORD`), never committed. See `application.yml` for the expected connection format.

## Roadmap

What's still ahead, per the course plan:
- Spring Security + bcrypt password hashing
- JWT-based authentication (sign-up, login, stateless sessions)
- Role-based access control, mapping expenses to individual users
- A JavaFX front end (login/signup, expense table, add/edit views, stats view)
- Deployment (Railway) and a packaged JAR