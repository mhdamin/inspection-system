# MUVS Fleet Management System - Complete Roadmap

## 📊 Project Status Overview

### ✅ Phase 1 - Critical Fixes (100% Complete)
- TypeScript type definitions
- Frontend-backend field mapping
- Seed data implementation
- Configuration centralization
- All compilation errors resolved

### ✅ Phase 2 - Security & Integration (100% Complete)
- JWT authentication (access + refresh tokens)
- Login page implementation
- Protected routes
- User service CRUD operations
- DTO validation
- VehicleChangeLog tracking
- Security configuration

### 🚧 Phase 3 - Enhanced Features & UX (36% Complete - 4/11 tasks)
**Current Progress:**
- ✅ Token refresh mechanism (access + refresh tokens)
- ✅ RefreshToken entity and repository
- ✅ Refresh endpoints in AuthController
- ✅ Frontend token refresh integration
- ⏳ Role-based access control (RBAC)
- ⏳ Account management
- ⏳ Audit log filtering

---

## 🎯 Phase 3 - Remaining Tasks (7/11 tasks)

### Option A: Role-Based Access Control (RBAC)
**Priority:** HIGH | **Time:** 2-3 hours

#### Backend Implementation
```java
// Add @PreAuthorize annotations to controllers

// Admin-only endpoints
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/users/{id}")

@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/vehicles/{id}")

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/users")

// User + Admin endpoints
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/api/vehicles")
```

**Files to modify:**
- VehicleController.java
- UserController.java
- ChecklistController.java
- SecurityConfig.java (enable method security)

#### Frontend Implementation
```typescript
// Hide UI elements based on roles
const isAdmin = auth.hasRole('ROLE_ADMIN');

{isAdmin && <UserManagementTab />}
{isAdmin && <DeleteButton />}
```

**Files to modify:**
- App.tsx (hide User Management tab for non-admins)
- VehicleManagement.tsx (hide delete buttons)
- UserManagement.tsx (hide for non-admins)

**Testing:**
- Login as admin → verify all features visible
- Login as regular user → verify restricted features hidden
- Try accessing admin endpoint as user → expect 403

---

### Option B: Account Management
**Priority:** MEDIUM | **Time:** 2-3 hours

#### Backend Endpoints
```java
@GetMapping("/api/account/profile")
UserResponseDTO getProfile(Principal principal)

@PutMapping("/api/account/profile")
UserResponseDTO updateProfile(@RequestBody ProfileUpdateDTO dto, Principal principal)

@PutMapping("/api/account/password")
void changePassword(@RequestBody ChangePasswordDTO dto, Principal principal)

@DeleteMapping("/api/account")
void deactivateAccount(Principal principal)
```

**New DTOs:**
- ProfileUpdateRequestDTO.java
- ChangePasswordRequestDTO.java

**New Controller:**
- AccountController.java

**New Service:**
- AccountService.java
- AccountServiceImpl.java

#### Frontend Components
**New files:**
- frontend/components/Account/AccountProfile.tsx
- frontend/components/Account/ChangePassword.tsx
- frontend/components/Account/index.tsx

**Features:**
- View profile (username, email, roles, created date)
- Edit profile form
- Change password form (old + new + confirm)
- Deactivate account button

**Testing:**
- Update profile with valid data
- Change password with correct old password
- Try changing password with wrong old password
- Deactivate account

---

### Option C: Audit Log Filtering
**Priority:** MEDIUM | **Time:** 2 hours

#### Backend Implementation
```java
// VehicleChangeLogService
List<VehicleChangeLogResponseDTO> getActivitiesFiltered(
    String staffName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    ChangeType changeType,
    Pageable pageable
);
```

**New DTOs:**
- ActivityFilterRequestDTO.java (with validation)

**Files to modify:**
- VehicleChangeLogRepository.java (add query methods)
- VehicleChangeLogService.java
- VehicleChangeLogServiceImpl.java
- VehicleChangeLogController.java (new endpoint)

#### Frontend Implementation
**Files to modify:**
- AuditTrail.tsx

**Features:**
- Wire up date pickers to backend
- Add action type dropdown (TEMPORARY, RETURN, PERMANENT)
- Add staff name filter input
- Implement pagination controls
- Add "Clear Filters" button
- Show loading state during filtering

**Testing:**
- Filter by date range
- Filter by action type
- Filter by staff name
- Combine multiple filters
- Test pagination
- Clear all filters

---

## 📅 Phase 4 - Testing & Quality Assurance

### Unit Testing
**Priority:** HIGH | **Time:** 4-5 hours

#### Backend Tests
```
src/test/java/
├── service/
│   ├── VehicleServiceTest.java
│   ├── UserServiceTest.java
│   ├── ChecklistServiceTest.java
│   ├── RefreshTokenServiceTest.java
│   └── SubChecklistServiceTest.java
├── controller/
│   ├── AuthControllerTest.java
│   ├── VehicleControllerTest.java
│   └── UserControllerTest.java
└── security/
    ├── JwtUtilTest.java
    └── JwtAuthenticationFilterTest.java
```

**Test coverage targets:**
- Service layer: 80%+
- Controller layer: 70%+
- Security components: 90%+

**Tools:**
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

#### Frontend Tests
```
frontend/__tests__/
├── components/
│   ├── Login.test.tsx
│   ├── Dashboard.test.tsx
│   ├── VehicleManagement.test.tsx
│   └── UserManagement.test.tsx
└── utils/
    ├── auth.test.ts
    └── config.test.ts
```

**Test coverage targets:**
- Components: 70%+
- Utilities: 80%+

**Tools:**
- Vitest
- React Testing Library
- MSW (Mock Service Worker)

---

### Integration Testing
**Priority:** MEDIUM | **Time:** 3-4 hours

#### API Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Test
    void loginAndRefreshToken_Success()

    @Test
    void accessProtectedEndpoint_WithValidToken_Success()

    @Test
    void accessProtectedEndpoint_WithExpiredToken_Unauthorized()
}
```

**Test scenarios:**
- Complete authentication flow
- Token refresh flow
- Protected endpoint access
- Role-based access control
- CRUD operations end-to-end

---

### End-to-End Testing
**Priority:** LOW | **Time:** 4-5 hours

**Tools:**
- Playwright or Cypress

**Test scenarios:**
- User login and logout
- Create/update/delete vehicles
- Create checklists
- Vehicle change tracking
- Audit trail viewing
- Role-based UI restrictions

---

## 🚀 Phase 5 - Production Deployment

### Database Migration
**Priority:** HIGH | **Time:** 2-3 hours

**Tasks:**
- Switch from H2 to PostgreSQL/MySQL
- Create production database schema
- Set up connection pooling (HikariCP configuration)
- Database backup strategy
- Migration scripts

**Files to modify:**
- application.properties
- pom.xml (add PostgreSQL/MySQL driver)

**Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/muvs_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

### Security Hardening
**Priority:** HIGH | **Time:** 2-3 hours

#### HTTPS Configuration
- Generate SSL certificate (Let's Encrypt)
- Configure Spring Boot for HTTPS
- Redirect HTTP to HTTPS

#### JWT Security
- Change JWT secret key
- Reduce access token expiration (15 min → 5 min)
- Implement token rotation
- Add token blacklist for revoked tokens

#### CORS Configuration
- Restrict allowed origins to production domains
- Configure allowed methods and headers
- Set appropriate max age

#### Headers Security
```java
http.headers()
    .contentSecurityPolicy("default-src 'self'")
    .xssProtection()
    .frameOptions().deny()
    .httpStrictTransportSecurity()
```

---

### Environment Configuration
**Priority:** HIGH | **Time:** 1-2 hours

**Create environment-specific configs:**
```
application-dev.properties
application-staging.properties
application-prod.properties
```

**Environment variables:**
```bash
export JWT_SECRET=<secure-random-secret>
export DB_USERNAME=<db-user>
export DB_PASSWORD=<db-password>
export ALLOWED_ORIGINS=https://your-domain.com
```

---

### Docker Containerization
**Priority:** MEDIUM | **Time:** 2-3 hours

**Files to create:**
- Dockerfile (backend)
- Dockerfile (frontend)
- docker-compose.yml
- .dockerignore

**Backend Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: muvs_db
      POSTGRES_USER: muvs_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres-data:
```

---

### CI/CD Pipeline
**Priority:** MEDIUM | **Time:** 3-4 hours

#### GitHub Actions Workflow
**.github/workflows/ci-cd.yml:**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build backend
        run: mvn clean package -DskipTests
      - name: Build frontend
        run: cd frontend && npm ci && npm run build

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to production
        # Add deployment steps
```

---

### Monitoring & Logging
**Priority:** MEDIUM | **Time:** 2-3 hours

#### Spring Boot Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Endpoints to expose:**
- /actuator/health
- /actuator/metrics
- /actuator/prometheus (for Prometheus monitoring)

#### Logging Configuration
```properties
logging.level.root=INFO
logging.level.com.muvs.inspection_system=DEBUG
logging.file.name=logs/application.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

#### Monitoring Tools
- Prometheus (metrics collection)
- Grafana (visualization)
- ELK Stack (Elasticsearch, Logstash, Kibana) for log aggregation

---

## 🎨 Phase 6 - Additional Features (Optional)

### Two-Factor Authentication (2FA)
**Time:** 4-5 hours

**Implementation:**
- TOTP (Time-based OTP) using Google Authenticator
- SMS-based OTP
- Backup codes

**Libraries:**
- google-authenticator library
- QR code generation

---

### Password Reset Flow
**Time:** 2-3 hours

**Features:**
- "Forgot Password" link on login page
- Email with reset token
- Password reset form
- Token expiration (1 hour)

**New endpoints:**
- POST /api/auth/forgot-password
- POST /api/auth/reset-password

---

### Email Notifications
**Time:** 3-4 hours

**Use cases:**
- Password reset emails
- Account creation confirmation
- Vehicle change notifications
- Inspection due reminders

**Tools:**
- Spring Mail
- Thymeleaf (email templates)
- SendGrid or Amazon SES

---

### File Upload & Storage
**Time:** 3-4 hours

**Features:**
- Upload defect images
- Store vehicle documents
- PDF export for checklists

**Implementation:**
- Amazon S3 for storage
- or local file storage with serve static files

---

### Real-time Notifications
**Time:** 4-5 hours

**Features:**
- WebSocket connection
- Live updates for vehicle changes
- Notification bell icon
- Toast notifications

**Tools:**
- Spring WebSocket
- STOMP protocol
- SockJS fallback

---

### Advanced Reporting
**Time:** 5-6 hours

**Features:**
- Vehicle usage reports
- Maintenance history reports
- User activity reports
- Export to PDF/Excel

**Libraries:**
- Apache POI (Excel)
- iText or JasperReports (PDF)

---

### Mobile Responsive Enhancements
**Time:** 2-3 hours

**Improvements:**
- Mobile navigation menu
- Touch-optimized UI components
- Responsive tables (horizontal scroll or card view)
- Mobile-specific layouts

---

### Search & Filtering
**Time:** 3-4 hours

**Features:**
- Global search across vehicles, users, checklists
- Advanced filters with multiple criteria
- Search history
- Saved searches

**Implementation:**
- Elasticsearch for full-text search
- or database full-text search

---

## 📊 Estimated Timeline

| Phase | Tasks | Estimated Time | Priority |
|-------|-------|----------------|----------|
| Phase 1 | Critical Fixes | ✅ Complete | HIGH |
| Phase 2 | Security & Integration | ✅ Complete | HIGH |
| Phase 3 | Enhanced Features | 7-10 hours remaining | HIGH |
| Phase 4 | Testing & QA | 11-14 hours | HIGH |
| Phase 5 | Production Deployment | 12-15 hours | HIGH |
| Phase 6 | Additional Features | 20-30 hours | MEDIUM |
| **Total Remaining** | | **50-69 hours** | |

---

## 🎯 Recommended Priorities

### Immediate (Next 2-3 days)
1. ✅ Fix CORS error (Current blocker)
2. 🔄 Complete Phase 3 features
   - RBAC implementation
   - Account management
   - Audit log filtering

### Short-term (Next 1-2 weeks)
3. Phase 4 - Testing
   - Unit tests
   - Integration tests
   - Fix discovered bugs

### Medium-term (Next 3-4 weeks)
4. Phase 5 - Production Deployment
   - Database migration
   - Security hardening
   - Docker setup
   - CI/CD pipeline

### Long-term (Optional)
5. Phase 6 - Additional Features
   - 2FA
   - Email notifications
   - Advanced reporting

---

## 🛠️ Development Environment Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+
- Git

### Backend Setup
```bash
cd D:\Code\Java-Project\muvs-inspection-system
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Database
- H2 in-memory (development)
- PostgreSQL 15+ (production)

---

## 📝 Documentation

### API Documentation
- Swagger/OpenAPI integration
- API versioning strategy
- Request/response examples

### User Documentation
- User manual
- Admin guide
- FAQ section

### Developer Documentation
- Architecture overview
- Code style guide
- Contribution guidelines

---

## 🔐 Security Checklist

- [x] JWT authentication
- [x] Password encryption (BCrypt)
- [x] Token expiration
- [ ] HTTPS in production
- [ ] CORS properly configured
- [ ] SQL injection prevention (JPA/Hibernate)
- [ ] XSS prevention
- [ ] CSRF protection
- [ ] Input validation
- [ ] Rate limiting
- [ ] Security headers
- [ ] Regular security audits

---

**Last Updated:** 2025-11-23
**Current Phase:** Phase 3 - Enhanced Features & UX (36% Complete)
**Next Action:** Fix CORS error, then proceed with RBAC implementation
