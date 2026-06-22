# 🔐 Auth API — Spring Boot 4 + JWT + Redis + Rate Limiting

A RESTful authentication and authorization API built with **Spring Boot 4.1**, designed as a solid foundation for projects that need secure login, role-based access control, token invalidation, and protection against endpoint abuse.

This project was built as a portfolio piece to demonstrate backend development best practices in Java: layered architecture, JWT-based security, integration testing with Testcontainers, OpenAPI documentation, and Docker containerization.

---

## ✨ Features

- ✅ **JWT Authentication** (access token + refresh token)
- ✅ **Roles and authorization** with Spring Security (`USER`, `ADMIN`)
- ✅ **Real logout** via token blacklist stored in **Redis**
- ✅ **Rate limiting** on sensitive endpoints (`/login`, `/register`) using **Bucket4j**
- ✅ **Interactive documentation** with Swagger / OpenAPI 3
- ✅ **Centralized exception handling**
- ✅ **Integration tests** with `MockMvc` and **Testcontainers** (PostgreSQL)
- ✅ **Full containerization** with Docker and Docker Compose (app + Postgres + Redis)

---

## 🛠️ Tech Stack

| Category           | Technology                                    |
|----------------------|------------------------------------------------|
| Language             | Java 21                                        |
| Framework            | Spring Boot 4.1                                |
| Security             | Spring Security + JWT (jjwt 0.12.6)            |
| Database             | PostgreSQL + Spring Data JPA                   |
| Cache / Blacklist    | Redis (Spring Data Redis)                      |
| Rate Limiting        | Bucket4j                                       |
| Documentation        | SpringDoc OpenAPI (Swagger UI)                 |
| Testing              | JUnit 5, MockMvc, Testcontainers, H2           |
| Build                | Gradle                                         |
| Containers           | Docker / Docker Compose                        |

---

## 🏗️ Project Architecture

```
src/main/java/com/bodev/auth_api
├── config/         # Security, JWT, Rate Limiting, OpenAPI, Web config
├── controller/     # REST endpoints (Auth, Admin)
├── dto/            # Data transfer objects (Request/Response)
├── entity/         # JPA entities (User)
├── exception/      # Custom exceptions + global error handling
├── repository/     # JPA repositories
└── service/        # Business logic (Auth, JWT, RateLimiter, Blacklist)
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker and Docker Compose

### 1. Clone the repository
```bash
git clone https://github.com/your-username/auth_api.git
cd auth_api
```

### 2. Start the dependencies (PostgreSQL + Redis)
```bash
docker-compose up -d
```

### 3. Run the application
```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

### 4. Run the tests
```bash
./gradlew test
```

### 5. Build and run everything with Docker (optional)
```bash
docker build -t auth-api .
docker run -p 8080:8080 auth-api
```

---

## 📚 API Documentation

Once the project is running, the interactive Swagger documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

### Main endpoints

| Method | Endpoint               | Description                              | Access        |
|--------|-------------------------|--------------------------------------------|---------------|
| POST   | `/api/auth/register`   | Registers a new user                       | Public        |
| POST   | `/api/auth/login`      | Authenticates and returns JWT tokens       | Public        |
| POST   | `/api/auth/refresh`    | Renews the access token                    | Public*       |
| POST   | `/api/auth/logout`     | Invalidates the current token (blacklist)  | Authenticated |
| GET    | `/api/auth/hello`      | Test endpoint                              | Public        |
| GET    | `/api/admin/dashboard` | Admin dashboard                            | `ADMIN` role  |
| GET    | `/api/admin/users`     | User listing                               | `ADMIN` role  |

\* Requires a valid refresh token in the `Authorization` header.

### Example: user registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "password": "secret123"
  }'
```

### Example: login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "secret123"
  }'
```

The response includes `token` (access token) and `refreshToken`.

---

## 🔒 Security

- Passwords are stored using **BCrypt**.
- JWT tokens are **stateless** (no server-side sessions).
- **Logout** works by invalidating the token via a Redis blacklist until its natural expiration.
- Public registration **always assigns the `USER` role**; administrative roles can only be granted through protected endpoints.
- Sensitive endpoints (`/login`, `/register`) are protected against abuse with **per-IP rate limiting**.

---

## 🧪 Testing

The project includes integration tests that spin up the full Spring context with `@SpringBootTest` and `MockMvc`, using the `test` profile (in-memory H2 database) to keep test execution isolated from the development environment.

```bash
./gradlew clean test
```

---

## 📌 Roadmap

- [ ] Email confirmation on registration
- [ ] Password recovery (forgot password)
- [ ] Pagination and search on `/api/admin/users`
- [ ] Metrics with Actuator + Prometheus
- [ ] CI/CD pipeline with GitHub Actions

---

## 👤 Author

**Your Name**
[GitHub](https://github.com/your-username) · [LinkedIn](https://linkedin.com/in/your-username) · youremail@email.com

---

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.
