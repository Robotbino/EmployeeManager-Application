# Employee Manager Application

A Spring Boot REST API with JWT authentication for managing employee records. It is the backend half
of a two-repo project; the Angular client lives in
[`EmployeeManager-Application-Frontend`](https://github.com/Robotbino/EmployeeManager-Application-Frontend).
Together they are an exercise in stateless auth: register or log in to get a signed token, then send
it as a Bearer header on every call.

This project demonstrates a Spring Security 6 filter chain assembled by hand (a `OncePerRequestFilter`,
a `DaoAuthenticationProvider`, BCrypt), JPA entities on MySQL, and plain CRUD over one resource. It is
a learning project and says so: [Known Limitations](#known-limitations) lists what is still open, and
[`docs/architecture-mvp.html`](docs/architecture-mvp.html) scopes the four changes that would take it
to an MVP, starting with the one that makes the employee endpoints actually require a token.

[![Java](https://img.shields.io/badge/Java-17-ED8B00)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3.3-6DB33F)](https://spring.io/projects/spring-security)
[![JJWT](https://img.shields.io/badge/JJWT-0.11.5-000000)](https://github.com/jwtk/jjwt)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)](https://www.mysql.com/)
[![Lombok](https://img.shields.io/badge/Lombok-1.18.34-BC4521)](https://projectlombok.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.9%20%28wrapper%29-C71A36)](https://maven.apache.org/)
[![Client](https://img.shields.io/badge/Client-Angular-DD0031)](https://github.com/Robotbino/EmployeeManager-Application-Frontend)

> **Live demo:** _none_ (runs locally) · **Angular client:** [`Robotbino/EmployeeManager-Application-Frontend`](https://github.com/Robotbino/EmployeeManager-Application-Frontend) · **Docs:** [Architecture & MVP roadmap](docs/architecture-mvp.html) (GitHub shows the HTML as source; clone the repo to read it)

---

## Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Installation & Running](#installation--running)
- [API Endpoints](#api-endpoints)
- [Security Features](#security-features)
- [Configuration](#configuration)
- [Key Dependencies](#key-dependencies)
- [Testing](#testing)
- [Common Issues & Solutions](#common-issues--solutions)
- [Known Limitations](#known-limitations)
- [Future Enhancements](#future-enhancements)
- [Related](#related)

---

## Features

- **JWT Authentication**: register or log in and receive a signed HS256 token (`access_token`) that is
  valid for 24 hours
- **User Registration & Login**: `POST /api/v1/auth/register` and `POST /api/v1/auth/authenticate`,
  with passwords hashed by BCrypt before they are stored
- **Stateless Security**: no HTTP session. `JwtAuthenticationFilter` reads the `Authorization: Bearer`
  header on every request, checks the token's subject and expiry against the user loaded from the
  database, and puts that user into the `SecurityContext`
- **Employee Management**: full CRUD over employee records, with a random UUID `employeeCode` assigned
  by the server when a record is created
- **Roles**: a `Role` enum with `USER` and `ADMIN`, stored on each account. Every registration is a
  `USER`, nothing grants `ADMIN`, and no endpoint checks a role yet. The employee endpoints are open to
  anyone today (see [Known Limitations](#known-limitations))
- **RESTful API**: two controllers, seven endpoints, JSON in and out
- **MySQL Database**: persistent storage through Spring Data JPA and Hibernate
- **CORS for the Angular client**: a `CorsFilter` bean allows `http://localhost:4200`, with credentials

## Technology Stack

- **Java 17** (`java.version` in `pom.xml`)
- **Spring Boot 3.3.4**: the Web, Security and Data JPA starters
- **Spring Security 6.3.3** (version managed by the Boot parent): one `SecurityFilterChain`, stateless
  sessions, CSRF off
- **Spring Data JPA** on **Hibernate 6.5.3**: entities, repositories and derived queries
- **MySQL** through Connector/J 8.3.0 (managed by Boot). The setup below assumes MySQL 8
- **JJWT 0.11.5** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`): building, signing and parsing tokens
- **Lombok 1.18.34**: constructors, builders, getters and setters
- **Maven**: the wrapper (`mvnw`, Maven 3.9.9) is committed, so no local Maven install is needed

## Project Structure

```
.
├── docs/
│   └── architecture-mvp.html                 # As-built architecture and the four scoped MVP changes
├── mvnw · mvnw.cmd · .mvn/wrapper/           # Maven wrapper (Maven 3.9.9)
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/EmployeeManager/Application/
    │   │   ├── EmployeeManagerApplication.java   # Entry point; also declares the CorsFilter bean
    │   │   ├── Models/
    │   │   │   ├── AuthUser.java                 # Account entity (table auth_user); implements UserDetails
    │   │   │   ├── Employee.java                 # Employee entity (table employee)
    │   │   │   └── Role.java                     # USER, ADMIN
    │   │   ├── Repositories/
    │   │   │   ├── AuthRepository.java           # findByEmail
    │   │   │   └── employeeRepo.java             # findEmployeeById, deleteEmployeeById
    │   │   ├── Services/
    │   │   │   ├── AuthenticationService.java    # register / authenticate
    │   │   │   ├── EmployeeService.java          # CRUD; assigns the UUID employeeCode
    │   │   │   └── JwtService.java               # Build, sign, parse and validate tokens
    │   │   ├── controllers/
    │   │   │   ├── AuthenticationController.java # /api/v1/auth/**
    │   │   │   └── EmployeeController.java       # /employee/**
    │   │   ├── configuration/
    │   │   │   ├── ApplicationConfig.java        # UserDetailsService, DaoAuthenticationProvider, BCrypt
    │   │   │   ├── SecurityConfiguration.java    # The SecurityFilterChain
    │   │   │   └── JwtAuthenticationFilter.java  # OncePerRequestFilter that reads the Bearer header
    │   │   ├── authentication/
    │   │   │   ├── AuthenticationRequest.java    # Login request DTO
    │   │   │   ├── AuthenticationResponse.java   # { "access_token": ... }
    │   │   │   └── RegisterRequest.java          # Registration request DTO
    │   │   └── Exceptions/
    │   │       └── UserNotFoundException.java    # Thrown for an unknown employee id (no handler yet)
    │   └── resources/
    │       └── application.yml                   # Port, datasource, JPA and logging settings
    └── test/java/com/EmployeeManager/Application/
        └── EmployeeManagerApplicationTests.java  # The generated contextLoads test
```

Package folder casing is mixed (`Models`, `Services` next to `controllers`, `configuration`); the tree
above matches the repo as it is.

## Prerequisites

Before running this application, make sure you have:

- **Java 17** or newer (a JDK). `java.version` in `pom.xml` is 17
- **MySQL 8** running locally on port 3306
- **Maven** is optional. The repo ships the Maven wrapper (`./mvnw` on macOS/Linux, `mvnw.cmd` on
  Windows), which downloads Maven 3.9.9 on first use
- An IDE (IntelliJ IDEA, Eclipse or VS Code) if you want one; none is required

## Database Setup

1. Create the database the app expects (note: no underscore in the name):
```sql
CREATE DATABASE employeemanager;
```

2. The connection settings live in `src/main/resources/application.yml` (there is no
   `application.properties`). As committed:
```yaml
server:
  port: 8181

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employeemanager
    username: root
    password: password            # placeholder: change it, or override it at launch
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update            # Hibernate creates and alters the tables at startup
    show-sql: true
```

Either edit `username` and `password` to match your MySQL user, or leave the file alone and override
them when you launch, for example with the environment variables `SPRING_DATASOURCE_USERNAME` and
`SPRING_DATASOURCE_PASSWORD`. With `ddl-auto: update`, the `auth_user` and `employee` tables are
created on the first run; there are no migration scripts.

## Installation & Running

1. **Clone the repository**
```bash
git clone https://github.com/Robotbino/EmployeeManager-Application.git
cd EmployeeManager-Application
```

2. **Build the project**
```bash
./mvnw clean install -DskipTests      # Windows: mvnw.cmd clean install -DskipTests
```
`-DskipTests` is deliberate. The only test boots the whole application context against the MySQL
in `application.yml`, so a plain `clean install` fails when the database is not up (see
[Testing](#testing)).

3. **Run the application**
```bash
./mvnw spring-boot:run                # Windows: mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8181` (`server.port` in `application.yml`). The Angular
client is expected on `http://localhost:4200`, which is the only origin the CORS filter allows.

## API Endpoints

### Authentication Endpoints

Both endpoints are public (`permitAll`) and both return `200 OK` with the same body: a signed JWT under
the key `access_token`.

#### Register a New User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

Creates an account with role `USER` and the BCrypt hash of the password, then returns a token
straight away. There is no uniqueness check on `email`: registering the same address twice creates
two rows (see [Known Limitations](#known-limitations)).

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Login
```http
POST /api/v1/auth/authenticate
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

Checks the credentials through Spring's `AuthenticationManager` and returns a fresh token.

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Employee Endpoints

> **Note:** these endpoints are **currently open**. `SecurityConfiguration.java` declares
> `/employee/**` as `permitAll`, so they answer with or without a token. When a token is sent,
> `JwtAuthenticationFilter` validates it and the request runs as that user; when none is sent, the
> request still goes through. Requiring the token is the first change (CO-00) in
> [`docs/architecture-mvp.html`](docs/architecture-mvp.html). The header below is what the Angular
> client already sends, and what the API will require once CO-00 lands:
> ```
> Authorization: Bearer your_jwt_token_here
> ```

An employee looks like this in every response:
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "jobTitle": "Software Engineer",
  "imageUrl": "https://example.com/image.jpg",
  "phone": "+1234567890",
  "employeeCode": "3f1c2e7a-9b4d-4c1e-8a2f-5d6e7f8a9b0c"
}
```

`employeeCode` is a random UUID set by the server when the employee is created. The column is
`updatable = false`, so it never changes afterwards.

#### Get All Employees
```http
GET /employee/all
Authorization: Bearer {token}
```
`200 OK` with a JSON array of every employee. There is no pagination.

#### Get Employee by ID
```http
GET /employee/find/{id}
Authorization: Bearer {token}
```
`200 OK` with the employee. An unknown id throws `UserNotFoundException`, which has no handler, so
the response is a `500`, not a `404`.

#### Add New Employee
```http
POST /employee/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "jobTitle": "Software Engineer",
  "phone": "+1234567890",
  "imageUrl": "https://example.com/image.jpg"
}
```
`201 Created` with the saved employee, including its new `id` and `employeeCode`. Any
`employeeCode` in the request is overwritten.

#### Update Employee
```http
PUT /employee/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "jobTitle": "Senior Software Engineer",
  "phone": "+1234567890",
  "imageUrl": "https://example.com/image.jpg"
}
```
`200 OK` with the updated employee. Send the whole record; the `id` must belong to an existing row.

#### Delete Employee
```http
DELETE /employee/delete/{id}
Authorization: Bearer {token}
```
`200 OK` with an empty body.

## Security Features

### JWT Token
- Tokens are generated on successful registration or login (`JwtService.generateToken`)
- Signed with HS256 using a 256-bit key. The subject is the user's email, `iat` is set, and the
  token expires **24 hours** after it is issued (`1000 * 60 * 60 * 24` in `JwtService.java`)
- `isTokenValid` checks two things: the subject equals the username of the user loaded from the
  database, and the token has not expired
- `JwtAuthenticationFilter` skips `/api/v1/auth/**`, lets requests with no `Bearer` header through
  untouched, and otherwise loads the user and sets the `SecurityContext`
- Sessions are `STATELESS` and CSRF is disabled; there are no cookies to protect

### Password Hashing
- Passwords are hashed with `BCryptPasswordEncoder` (declared in `ApplicationConfig.java`) before
  they are stored in `auth_user.password`
- No plain-text passwords are stored. Login compares the hash through `DaoAuthenticationProvider`

### Authentication Flow
1. User registers or logs in with credentials
2. Server validates the credentials and generates a JWT
3. Client stores the token and sends it as `Authorization: Bearer <token>`. The Angular client does
   this with an HTTP interceptor (see the "As built" sheet in `docs/architecture-mvp.html`)
4. On each request that carries a token, the filter validates it and the request runs as that user.
   Today no employee endpoint rejects a request that carries no token

## Configuration

### JWT Secret Key
The signing key is the constant `My_Secret_Key` in `JwtService.java`: a Base64 string that is decoded
into an HMAC key with `Keys.hmacShaKeyFor`. It is committed to the repo, so treat it as public. For
anything beyond local use:
1. Move the secret to `application.yml` and feed it from the environment:
```yaml
jwt:
  secret: ${JWT_SECRET}
```

2. Read it in `JwtService.java` with `@Value`, and generate a new key rather than reusing the one in
   the history:
```java
@Value("${jwt.secret}")
private String secretKey;
```

### Token Expiration
To change the token lifetime, modify this line in `JwtService.java`:
```java
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
// Current: 24 hours (1000 ms * 60 s * 60 min * 24 h)
```

### Port and Logging
`server.port` is `8181`. The `logging.level` block in `application.yml` sets the application package,
`org.hibernate.SQL`, `org.springframework.transaction`, `org.springframework.jdbc.datasource` and
`org.springframework.security` to `DEBUG`, and `org.hibernate.type.descriptor.sql.BasicBinder` to
`TRACE`, with `show-sql` and `format_sql` on as well. That is useful while learning and far too
chatty for anything else; turn it down before deploying.

## Key Dependencies

| Artifact | Version | Scope | Why it is here |
|---|---|---|---|
| `spring-boot-starter-web` | 3.3.4 (parent) | compile | REST controllers, Jackson |
| `spring-boot-starter-security` | 3.3.4 (parent) | compile | The filter chain, `AuthenticationManager`, BCrypt |
| `spring-boot-starter-data-jpa` | 3.3.4 (parent) | compile | `JpaRepository`, Hibernate |
| `mysql-connector-j` | 8.3.0 (managed) | runtime | JDBC driver |
| `io.jsonwebtoken:jjwt-api` | 0.11.5 | compile | JWT API |
| `io.jsonwebtoken:jjwt-impl` | 0.11.5 | runtime | JWT implementation |
| `io.jsonwebtoken:jjwt-jackson` | 0.11.5 | runtime | JSON (de)serialisation of claims |
| `org.projectlombok:lombok` | 1.18.34 | provided | Boilerplate |
| `spring-boot-starter-test` | 3.3.4 (parent) | test | JUnit 5, Spring test support |
| `spring-security-test` | 6.3.3 (managed) | test | Declared, not yet used by any test |

## Testing

### Automated tests
There is one test file, `src/test/java/com/EmployeeManager/Application/EmployeeManagerApplicationTests.java`,
and it is the Spring Initializr default: a `@SpringBootTest` with an empty `contextLoads()`. It has no
test profile and no in-memory database, so it boots the full context against the MySQL configured in
`application.yml` and fails when that database is not running. Nothing exercises the controllers,
the JWT filter or the services yet, and `spring-security-test` is on the classpath but unused.

```bash
./mvnw test        # needs MySQL up, with the credentials from application.yml
```

There is no CI; nothing runs on push.

### Testing with Postman

1. **Register a user**: send a POST request to `/api/v1/auth/register`
2. **Copy the token** from the `access_token` field in the response
3. **Set up authorisation**: in Postman, go to the Authorization tab
   - Type: Bearer Token
   - Token: paste your JWT
4. **Call the employee endpoints**: they respond with or without the token today (see
   [Known Limitations](#known-limitations)). Once CO-00 lands, a missing or expired token will be
   rejected

## Common Issues & Solutions

### Issue: Database Connection Failed
**Solution**: verify MySQL is running on `localhost:3306`, that the `employeemanager` database exists,
and that the credentials in `application.yml` (not `application.properties`) match your MySQL user.

### Issue: `mvnw clean install` fails in the test phase
**Solution**: the `contextLoads` test needs the database. Start MySQL first, or build with
`-DskipTests`.

### Issue: 401 or 403 on a request
**Solution**: only paths outside `/api/v1/auth/**` and `/employee/**` require a token today, so the
employee endpoints will not produce this. If you do see it, check the header reads
`Authorization: Bearer <token>` and the token is under 24 hours old.

### Issue: Token Expired
**Solution**: log in again to get a new token. Tokens expire after 24 hours.

### Issue: 500 from `GET /employee/find/{id}`
**Solution**: the id does not exist. `UserNotFoundException` has no exception handler yet, so an
unknown id surfaces as a `500` rather than a `404` (CO-01 in the architecture doc adds the handler).

### Issue: CORS Errors
**Solution**: CORS is configured by the `CorsFilter` bean in `EmployeeManagerApplication.java`, which
allows exactly `http://localhost:4200` with `GET`, `POST`, `PUT`, `DELETE` and `OPTIONS`.
`SecurityConfiguration.java` only switches CORS on. Add your origin to that list if your client runs
elsewhere.

## Known Limitations

These are stated plainly rather than hidden. The first four are the subject of
[`docs/architecture-mvp.html`](docs/architecture-mvp.html).

- **The employee endpoints are open.** `SecurityConfiguration.java` has `/employee/**` on
  `permitAll`, so every CRUD call works without a token and the whole JWT stack is decorative.
  Changing that one matcher to `authenticated()` is CO-00 in the architecture doc, a 15-minute fix
  that ships first because everything else inherits it.
- **No error contract.** `UserNotFoundException` has no `@ControllerAdvice`, so an unknown employee
  id returns a `500` instead of a `404`, and other failures return Spring Boot's default error body.
  CO-01 adds a global handler.
- **No pagination or search.** `GET /employee/all` returns every row. CO-02 replaces it with a paged,
  searchable endpoint.
- **No export.** CO-03 adds a CSV export endpoint.
- **The JWT signing key is hard-coded.** `My_Secret_Key` in `JwtService.java` is committed to the
  repo. Anyone with the source can mint a token the API will accept.
- **Database credentials are in the repo.** `application.yml` ships `root` / `password` with no
  environment indirection or profile.
- **Roles exist but nothing uses them.** Every registration is `USER`, nothing assigns `ADMIN`, and
  no endpoint checks a role.
- **Duplicate accounts are possible.** `register` does not check for an existing email and
  `AuthUser.email` has no unique constraint, so the same address can be registered twice. After
  that, `findByEmail` has more than one row to choose from.
- **A bad token is a server error, not a 401.** `JwtAuthenticationFilter` calls
  `jwtService.extractUsername` with no `try/catch`, so an expired or malformed Bearer token throws
  out of the filter and the client sees a `500`.
- **Schema by `ddl-auto: update`.** Hibernate creates and alters the tables at startup. There is no
  Flyway or Liquibase, so there is no record of schema changes and no way to roll one back.
- **Logging is wide open.** `application.yml` runs the application package, Hibernate SQL, Spring
  transactions, JDBC and Spring Security at `DEBUG`, with `show-sql` on and
  `org.hibernate.type.descriptor.sql.BasicBinder` at `TRACE`, which is meant to print every bound
  SQL parameter. Fine on a laptop, not anywhere else.
- **One test, and it needs a live MySQL.** Only the generated `contextLoads` exists, with no H2 and
  no test profile.
- **No CI.** Nothing runs on push.
- **No deployed instance.** Everything runs locally.

## Future Enhancements

The four MVP change orders come first, in the order the architecture doc gives them:

- [ ] **CO-00**: require a token on `/employee/**` (`permitAll` → `authenticated()`)
- [ ] **CO-01**: a global error contract (`@RestControllerAdvice`, `UserNotFoundException` → 404)
- [ ] **CO-02**: server-side pagination and search on the employee list
- [ ] **CO-03**: CSV export of the employee list

Then, in no particular order:

- [ ] Move the JWT secret and the database credentials out of the source and into the environment
- [ ] Role-based endpoint restrictions on top of CO-00, and a way to grant `ADMIN`
- [ ] A unique constraint on `auth_user.email`, with a clean error for a duplicate registration
- [ ] Refresh token implementation
- [ ] Email verification for registration
- [ ] Password reset functionality
- [ ] User profile management
- [ ] Flyway migrations in place of `ddl-auto: update`
- [ ] Integration tests against H2 so `mvnw test` does not need MySQL, and CI to run them on push
- [ ] API documentation with Swagger/OpenAPI
- [ ] Docker containerisation

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Related

- [`Robotbino/EmployeeManager-Application-Frontend`](https://github.com/Robotbino/EmployeeManager-Application-Frontend):
  the Angular client for this API
- [`Robotbino/gameStore-backend`](https://github.com/Robotbino/gameStore-backend) and
  [`Robotbino/gameStore`](https://github.com/Robotbino/gameStore): the larger Spring Boot + React
  project, also behind a JWT API
- [github.com/Robotbino](https://github.com/Robotbino): everything else

## Contact

Bino Hlongwana - email: HlongwanaBino@gmail.com

Project Link: [https://github.com/Robotbino/EmployeeManager-Application](https://github.com/Robotbino/EmployeeManager-Application)

## Acknowledgements

- Spring Boot Documentation
- JWT.io for JWT resources
- Stack Overflow community

---

**Note**: This is a learning project. The gaps are listed in [Known Limitations](#known-limitations)
rather than hidden. Before any real use it would also need HTTPS, rate limiting, input validation on
the request bodies, and structured logging and monitoring.
