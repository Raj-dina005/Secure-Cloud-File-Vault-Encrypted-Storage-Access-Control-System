# Secure Cloud File Vault

A full-stack secure file storage and sharing system built with **Java Spring Boot**, **Supabase Cloud Storage**, and **PostgreSQL**. Features JWT authentication, role-based access control, file sharing with expiry links, and a complete admin dashboard.

## Live Demo

**[https://secure-cloud-file-vault-encrypted.onrender.com](https://secure-cloud-file-vault-encrypted.onrender.com)**

> Note: Free tier - app may take 30-50 seconds to wake up on first visit.

---

## Features

- **JWT Authentication** - Secure login and registration
- **Cloud File Storage** - Files stored securely on Supabase
- **File Upload and Download** - Support for all file types up to 10MB
- **Share Links** - Generate expiring share links for files
- **Delete Files** - Soft delete with full audit trail
- **Admin Dashboard** - View all users, files, and audit logs
- **Audit Logging** - Every action tracked (upload, download, delete, share, login)
- **Delete Account** - Cascade delete all files and user data
- **Role-Based Access** - USER and ADMIN roles
- **Dockerized** - Runs consistently in any environment

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.5 |
| Security | Spring Security, JWT (jjwt 0.12.3) |
| Database | PostgreSQL (Supabase) |
| File Storage | Supabase Storage (S3-compatible) |
| Frontend | HTML, CSS, JavaScript (Thymeleaf) |
| Containerization | Docker, Docker Compose |
| Deployment | Render.com |
| Build Tool | Maven |

---

## Getting Started Locally

### Prerequisites
- Java 17+
- PostgreSQL
- Maven
- Docker (optional)

### 1. Clone the repository
```bash
git clone https://github.com/Raj-dina005/Secure-Cloud-File-Vault-Encrypted-Storage-Access-Control-System.git
cd Secure-Cloud-File-Vault-Encrypted-Storage-Access-Control-System
```

### 2. Create PostgreSQL database
```sql
CREATE DATABASE secure_vault;
```

### 3. Configure application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/secure_vault
spring.datasource.username=postgres
spring.datasource.password=your_password

supabase.url=your_supabase_url
supabase.key=your_supabase_service_role_key
supabase.bucket=vault-files
```

### 4. Run the application
```bash
mvn spring-boot:run
```

### 5. Open in browser
```
http://localhost:8080
```

---

## Run with Docker

```bash
# Build and start all containers
docker-compose up --build

# Stop containers
docker-compose down
```

---

## Setting Admin Role

After registering, run this SQL in your PostgreSQL client:
```sql
UPDATE users SET role='ADMIN' WHERE email='your@email.com';
```

---

## Project Structure

```
src/main/java/com/vault/secure_cloud_vault/
├── controller/       # REST API and Web controllers
│   ├── AuthController.java
│   ├── FileController.java
│   ├── ShareLinkController.java
│   ├── AdminController.java
│   ├── AuditController.java
│   └── WebController.java
├── service/          # Business logic
│   ├── UserService.java
│   ├── FileService.java
│   ├── ShareLinkService.java
│   ├── AuditLogService.java
│   └── StorageService.java
├── entity/           # JPA entities
│   ├── User.java
│   ├── FileMetadata.java
│   ├── ShareLink.java
│   └── AuditLog.java
├── repository/       # Spring Data JPA repositories
├── security/         # JWT and Spring Security config
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── SecurityConfig.java
└── dto/              # Data Transfer Objects
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| DELETE | `/api/auth/delete-account` | Delete user account |
| GET | `/api/files` | List user files |
| POST | `/api/files/upload` | Upload a file |
| GET | `/api/files/download/{id}` | Download a file |
| DELETE | `/api/files/delete/{id}` | Delete a file |
| POST | `/api/share/create/{fileId}` | Create share link |
| GET | `/api/share/{token}` | Access shared file |
| GET | `/api/admin/users` | Get all users (Admin only) |
| GET | `/api/audit/all` | Get all audit logs (Admin only) |

---

## Future Improvements

- Email verification on registration
- File encryption at rest
- Two-factor authentication
- File previews in browser
- Storage quota per user

---

## Author

**Raj Dina**
GitHub: [Raj-dina005](https://github.com/Raj-dina005)

---

## License

This project is open source and available under the MIT License.
