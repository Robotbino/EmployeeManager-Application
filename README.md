# Employee Manager Application

A Spring Boot REST API application with JWT authentication for managing employee records. This project demonstrates secure authentication, authorization, and CRUD operations using modern Spring Security practices.

## Features

- **JWT Authentication**: Secure token-based authentication system
- **User Registration & Login**: Complete authentication flow with encrypted passwords
- **Employee Management**: Full CRUD operations for employee records
- **Role-Based Access Control**: Support for USER and ADMIN roles
- **RESTful API**: Clean and organized API endpoints
- **MySQL Database**: Persistent data storage with JPA/Hibernate

## Technology Stack

- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Security 6**: For authentication and authorization
- **Spring Data JPA**: For database operations
- **MySQL**: Relational database
- **JWT (JSON Web Tokens)**: For secure authentication
- **Lombok**: To reduce boilerplate code
- **Maven**: Dependency management and build tool

## Project Structure

```
com.EmployeeManager.Application
├── Models
│   ├── AuthUser.java          # User entity for authentication
│   ├── Employee.java           # Employee entity
│   └── Role.java               # User roles enum (USER, ADMIN)
├── Repositories
│   ├── AuthRepository.java     # User data access
│   └── employeeRepo.java       # Employee data access
├── Services
│   ├── AuthenticationService.java  # Authentication business logic
│   ├── EmployeeService.java        # Employee business logic
│   └── JwtService.java             # JWT token operations
├── Controllers
│   ├── AuthenticationController.java  # Authentication endpoints
│   └── EmployeeController.java        # Employee CRUD endpoints
├── Configuration
│   ├── ApplicationConfig.java         # Spring configuration beans
│   ├── SecurityConfiguration.java     # Security settings
│   └── JwtAuthenticationFilter.java   # JWT request filter
└── Authentication
    ├── AuthenticationRequest.java     # Login request DTO
    ├── AuthenticationResponse.java    # Auth response with token
    └── RegisterRequest.java           # Registration request DTO
```

## Prerequisites

Before running this application, make sure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- MySQL 8.0+ installed and running
- An IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

## Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE employee_manager;
```

2. Configure your database connection in `application.properties` or `application.yml`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_manager
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Installation & Running

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/employee-manager.git
cd employee-manager
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8181` by default.

## API Endpoints

### Authentication Endpoints

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

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Employee Endpoints

> **Note:** All employee endpoints require authentication. Include the JWT token in the Authorization header:
> ```
> Authorization: Bearer your_jwt_token_here
> ```

#### Get All Employees
```http
GET /employee/all
Authorization: Bearer {token}
```

#### Get Employee by ID
```http
GET /employee/find/{id}
Authorization: Bearer {token}
```

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

#### Delete Employee
```http
DELETE /employee/delete/{id}
Authorization: Bearer {token}
```

## Security Features

### JWT Token
- Tokens are generated upon successful registration or login
- Token expiration: 24 minutes (configurable in `JwtService.java`)
- Tokens are validated on each protected endpoint request

### Password Encryption
- Passwords are encrypted using BCrypt before storage
- No plain text passwords are stored in the database

### Authentication Flow
1. User registers or logs in with credentials
2. Server validates credentials and generates JWT token
3. Client stores token and includes it in subsequent requests
4. Server validates token on each request to protected endpoints

## Configuration

### JWT Secret Key
The JWT secret key is currently hardcoded in `JwtService.java`. For production:
1. Move the secret to `application.properties`:
```properties
jwt.secret=your-256-bit-secret-key-here
```

2. Update `JwtService.java` to use `@Value` annotation:
```java
@Value("${jwt.secret}")
private String secretKey;
```

### Token Expiration
To change token expiration time, modify this line in `JwtService.java`:
```java
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
// Current: 24 minutes (1000ms * 60s * 24min)
```

## Key Dependencies

```xml
<!-- Spring Boot Starter Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Boot Starter Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Boot Starter Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

## Testing with Postman

1. **Register a user**: Send POST request to `/api/v1/auth/register`
2. **Copy the token** from the response
3. **Set up authorization**: In Postman, go to Authorization tab
   - Type: Bearer Token
   - Token: Paste your JWT token
4. **Test protected endpoints**: Send requests to employee endpoints

## Common Issues & Solutions

### Issue: Database Connection Failed
**Solution**: Verify MySQL is running and credentials in `application.properties` are correct.

### Issue: 401 Unauthorized
**Solution**: Ensure you're including the Bearer token in the Authorization header.

### Issue: Token Expired
**Solution**: Login again to get a new token. Tokens expire after 24 minutes.

### Issue: CORS Errors
**Solution**: CORS is enabled in `SecurityConfiguration.java`. Adjust if needed for your frontend origin.

## Future Enhancements

- [ ] Refresh token implementation
- [ ] Email verification for registration
- [ ] Password reset functionality
- [ ] Role-based endpoint restrictions
- [ ] User profile management
- [ ] Pagination for employee list
- [ ] Search and filter functionality
- [ ] API documentation with Swagger/OpenAPI
- [ ] Unit and integration tests
- [ ] Docker containerization

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).

## Contact

Bino Hlongwana - email: HlongwanaBino@gmail.com

Project Link: [https://github.com/yourusername/employee-manager](https://github.com/yourusername/employee-manager)

## Acknowledgments

- Spring Boot Documentation
- JWT.io for JWT resources
- Stack Overflow community

---

**Note**: This is a learning project. For production use, implement additional security measures such as:
- Environment variables for sensitive data
- Rate limiting
- Input validation and sanitization
- Comprehensive error handling
- Logging and monitoring
- HTTPS enforcement
