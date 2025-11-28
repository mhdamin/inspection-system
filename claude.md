# MUVS Inspection System - Claude Context Guide

**Last Updated:** 2025-11-24
**Current Phase:** Phase 4 - Testing & Quality Assurance
**Branch:** `feature/authentication`

---

## 📋 Project Overview

**FleetGuard** - A comprehensive vehicle rental inspection system for managing fleet vehicles, conducting inspections, and maintaining audit trails.

**Purpose:**
- Vehicle fleet management
- Pre-rental and post-rental inspections
- Digital checklists with photo documentation
- Audit trail tracking
- User management with role-based access control

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.4.0
- **Language:** Java 17+
- **Database:** H2 (development), PostgreSQL (production-ready)
- **Security:** Spring Security 6.x with JWT
- **ORM:** Spring Data JPA
- **Build Tool:** Maven
- **Port:** 8080

### Frontend
- **Framework:** React 18 + TypeScript
- **Build Tool:** Vite
- **Styling:** Tailwind CSS
- **Icons:** Lucide React
- **Port:** 3000 (dev), 5173 (vite default)

---

## 📁 Project Structure

```
muvs-inspection-system/
├── src/main/java/com/muvs/inspection_system/
│   ├── config/              # Security, CORS, JWT configuration
│   │   ├── SecurityConfig.java         # ✅ RBAC enabled, CORS configured
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtTokenProvider.java
│   ├── controller/          # REST API endpoints
│   │   ├── AuthController.java         # ✅ Login, register, refresh, logout
│   │   ├── VehicleController.java      # ✅ RBAC protected (admin only: create, update, delete)
│   │   ├── UserController.java         # ✅ RBAC protected (admin only)
│   │   └── VehicleChangeLogController.java
│   ├── entity/              # JPA entities
│   │   ├── User.java
│   │   ├── Vehicle.java               # ✅ Audit trail enabled
│   │   ├── RefreshToken.java          # ✅ Token refresh mechanism
│   │   └── VehicleChangeLog.java      # ✅ Audit logging
│   ├── repository/          # Data access layer
│   ├── service/             # Business logic
│   │   ├── impl/
│   │   │   ├── VehicleServiceImpl.java # ✅ Audit trail integration
│   │   │   └── UserServiceImpl.java
│   ├── dto/                 # Data transfer objects
│   └── security/            # Security utilities
├── frontend/
│   ├── components/
│   │   ├── Login.tsx               # ✅ JWT authentication
│   │   ├── Dashboard.tsx           # ✅ Real-time stats
│   │   ├── VehicleManagement.tsx   # ✅ RBAC UI restrictions
│   │   ├── UserManagement.tsx      # ✅ Admin only
│   │   ├── AuditTrail.tsx          # ✅ Change log viewer
│   │   └── Checklist/
│   │       └── ChecklistManager.tsx
│   ├── App.tsx                     # ✅ RBAC navigation
│   ├── config.ts                   # ✅ API config, auth utilities
│   └── types.ts
├── .claude/                 # Claude-specific documentation
│   ├── sessions/            # Session-specific changes
│   └── archive/             # Historical documentation
├── claude.md                # THIS FILE - Main context reference
├── ROADMAP.md              # Full project roadmap (Phase 1-6)
├── INTEGRATION.md          # Backend-frontend integration guide
└── CORS_FIX.md            # CORS configuration documentation
```

---

## 🔑 Key Files & Their Purposes

| File | Purpose | Last Modified |
|------|---------|---------------|
| `SecurityConfig.java` | Security configuration, CORS, method-level security | 2025-11-23 |
| `VehicleController.java` | Vehicle CRUD operations with RBAC | 2025-11-23 |
| `UserController.java` | User management (admin only) | 2025-11-23 |
| `VehicleServiceImpl.java` | Business logic with audit trail | 2025-11-23 |
| `App.tsx` | Main app with RBAC navigation | 2025-11-23 |
| `VehicleManagement.tsx` | Vehicle UI with role-based buttons | 2025-11-23 |
| `config.ts` | API config and auth utilities | 2025-11-23 |

---

## 🚀 Standard Commands

### Backend

```bash
# Build and compile
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build without tests
mvn clean install -DskipTests
```

**Backend URL:** `http://localhost:8080`
**H2 Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Frontend

```bash
# Install dependencies
cd frontend && npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

**Frontend URL:** `http://localhost:3000` (or port shown in terminal)

---

## 🔐 Authentication & Authorization

### JWT Token System

**Access Token:**
- Expiration: 15 minutes
- Stored in: `localStorage` as `accessToken`
- Used in: `Authorization: Bearer <token>` header

**Refresh Token:**
- Expiration: 7 days
- Stored in: `localStorage` as `refreshToken`
- Used to obtain new access tokens via `/api/auth/refresh`

### Demo Credentials

| Username | Password | Role | Capabilities |
|----------|----------|------|--------------|
| `admin` | `admin` | `ROLE_ADMIN` | Full access: Create/Edit/Delete vehicles, User management |
| `user` | `user` | `ROLE_USER` | Read access: View vehicles, View dashboard |

### RBAC Implementation

**Backend Protection:**
- `@EnableMethodSecurity(prePostEnabled = true)` in `SecurityConfig.java`
- `@PreAuthorize("hasRole('ADMIN')")` on admin-only endpoints

**Frontend Protection:**
- `auth.hasRole('ROLE_ADMIN')` checks in components
- Conditional rendering of admin features
- Hidden navigation items for unauthorized users

---

## 📡 API Endpoints

### Authentication (`/api/auth`)
```
POST   /api/auth/login           # Login (public)
POST   /api/auth/register        # Register new user (public)
POST   /api/auth/refresh         # Refresh access token (public)
POST   /api/auth/logout          # Logout and revoke refresh token (authenticated)
```

### Vehicles (`/api/vehicles`)
```
GET    /api/vehicles             # Get all vehicles (authenticated)
GET    /api/vehicles/{id}        # Get vehicle by ID (authenticated)
GET    /api/vehicles/stats       # Get vehicle statistics (authenticated)
GET    /api/vehicles/plate/{plateNumber}  # Find by plate (authenticated)
POST   /api/vehicles             # Create vehicle (ADMIN ONLY) ⚠️
PUT    /api/vehicles/{id}        # Update vehicle (ADMIN ONLY) ⚠️
DELETE /api/vehicles/{id}        # Delete vehicle (ADMIN ONLY) ⚠️
```

### Users (`/api/users`)
```
GET    /api/users                # Get all users (ADMIN ONLY) ⚠️
```

### Audit Trail (`/api/vehicle-changelogs`)
```
GET    /api/vehicle-changelogs   # Get all change logs (authenticated)
```

### Activities (`/api/activities`)
```
GET    /api/activities           # Get recent activities (authenticated)
```

---

## 🗂️ Database Schema

### Core Entities

**User**
- `id` (UUID, PK)
- `username` (String, unique)
- `password` (String, BCrypt hashed)
- `email` (String)
- `roles` (Set<Role>)
- Timestamps: `createdAt`, `updatedAt`

**Vehicle**
- `id` (UUID, PK)
- `plateNumber` (String, unique, indexed)
- `manufacturer`, `model`, `year`
- `status` (Available, Rented, Maintenance)
- Timestamps: `createdAt`, `updatedAt`

**RefreshToken**
- `id` (UUID, PK)
- `token` (String, unique, indexed)
- `user` (ManyToOne → User)
- `expiryDate` (Instant)

**VehicleChangeLog**
- `id` (UUID, PK)
- `changeType` (TEMPORARY, RETURN, PERMANENT)
- `oldVehicle`, `newVehicle` (ManyToOne → Vehicle)
- `oldVehiclePlate`, `newVehiclePlate`
- `staffName`, `reason`
- `timestamp` (LocalDateTime)

---

## 🔄 Development Progress

### ✅ Completed Phases

#### **Phase 1: Project Setup** (Completed)
- [x] Spring Boot backend initialization
- [x] React + TypeScript frontend setup
- [x] Database schema design
- [x] Basic CRUD operations

#### **Phase 2: Core Features** (Completed)
- [x] Vehicle management
- [x] User interface components
- [x] Dashboard with real-time stats
- [x] Checklist management (UI)

#### **Phase 3: Authentication & RBAC** (✅ COMPLETE)
- [x] JWT authentication implementation
- [x] Access token + Refresh token mechanism
- [x] Login/Logout functionality
- [x] Token auto-refresh
- [x] Backend RBAC with `@PreAuthorize`
- [x] Frontend role-based UI restrictions
- [x] CORS configuration fix
- [x] Security configuration hardening

**Note:** Advanced features (account management, audit filtering) moved to Phase 6 - Optional Features

### 🔜 Current Phase

#### **Phase 4: Testing & QA** (IN PROGRESS - 0% Complete)
- [ ] Backend unit tests (JUnit 5, Mockito)
  - [ ] Service layer tests
  - [ ] Controller layer tests
  - [ ] Security component tests
- [ ] Frontend unit tests (Vitest, React Testing Library)
  - [ ] Component tests
  - [ ] Utility function tests
- [ ] Integration tests
  - [ ] API endpoint tests
  - [ ] Authentication flow tests
- [ ] E2E tests (Optional - Playwright/Cypress)

### 🔜 Upcoming Phases

#### **Phase 5: Production Preparation**
- [ ] PostgreSQL migration
- [ ] Environment configuration
- [ ] Deployment scripts
- [ ] Monitoring setup

#### **Phase 6: Optional Features**
- [ ] Two-factor authentication
- [ ] Email notifications
- [ ] Advanced reporting
- [ ] Mobile app (React Native)

---

## 📝 Change Log - Claude Sessions

### Session 2025-11-24 (Phase 4 - Testing & QA Setup)

**✅ Completed:**

1. **Phase 3 Finalization**
   - Marked Phase 3 as complete
   - Updated project status documentation
   - **Reason:** All core RBAC and authentication features implemented

2. **Phase 4 Planning & Documentation**
   - Created `PHASE4_TESTING_PLAN.md` - Comprehensive testing strategy
   - Updated `claude.md` with Phase 4 goals
   - Identified testing priorities and roadmap
   - **Reason:** Establish clear testing standards and approach

3. **Backend Testing Infrastructure**
   - Modified: `pom.xml` - Added JaCoCo plugin (v0.8.11) for code coverage
   - Modified: `pom.xml` - Added Spring Security Test dependency
   - Created test directory structure: `src/test/java/com/muvs/inspection_system/`
   - **Reason:** Enable automated testing and coverage reporting

4. **Frontend Testing Infrastructure**
   - Modified: `frontend/package.json` - Added Vitest, React Testing Library, jsdom
   - Created: `frontend/vitest.config.ts` - Vitest configuration with coverage settings
   - Created: `frontend/__tests__/setup.ts` - Test environment setup
   - Created test directory structure: `frontend/__tests__/components/`, `frontend/__tests__/utils/`
   - **Reason:** Enable component and utility testing for React app

5. **Initial Unit Tests**
   - Created: `VehicleServiceImplTest.java` - 14 comprehensive tests for VehicleService
     - Tests cover: create, read, update, delete operations
     - Tests cover: error handling and edge cases
     - Tests cover: business logic validation
     - All 14 tests passing ✅
   - Created: `config.test.ts` - Comprehensive tests for auth utilities
     - Tests cover: token management, role checking, authentication status
     - Tests cover: logout flow, token refresh, error handling
   - **Reason:** Demonstrate testing best practices and establish baseline coverage

**Files Created:**
```
A  PHASE4_TESTING_PLAN.md
A  frontend/vitest.config.ts
A  frontend/__tests__/setup.ts
A  frontend/__tests__/utils/config.test.ts
A  src/test/java/com/muvs/inspection_system/service/impl/VehicleServiceImplTest.java
```

**Files Modified:**
```
M  claude.md
M  pom.xml (JaCoCo plugin, Spring Security Test)
M  frontend/package.json (testing dependencies and scripts)
```

**Test Results:**
- Backend: VehicleServiceImplTest - 14/14 tests passing ✅
- Code coverage infrastructure ready
- Frontend test infrastructure ready

**Next Steps:**
- Write additional backend service tests (UserService, RefreshTokenService)
- Write backend controller tests (AuthController, VehicleController)
- Write frontend component tests (Login, Dashboard, VehicleManagement)
- Generate and review code coverage reports
- Set up CI/CD pipeline for automated testing

---

### Session 2025-11-23 (Phase 3 - RBAC Implementation)

**✅ Completed:**

1. **CORS Configuration Fix**
   - Modified: `SecurityConfig.java`
   - Added `corsConfigurationSource()` bean with `allowedOriginPatterns`
   - Removed `@CrossOrigin(origins = "*")` from `AuthController.java`
   - **Reason:** Fixed "IllegalArgumentException: When allowCredentials is true, allowedOrigins cannot contain '*'"

2. **Backend RBAC Implementation**
   - Modified: `SecurityConfig.java` - Added `@EnableMethodSecurity(prePostEnabled = true)`
   - Modified: `VehicleController.java` - Added `@PreAuthorize("hasRole('ADMIN')")` to create, update, delete methods
   - Modified: `UserController.java` - Added `@PreAuthorize("hasRole('ADMIN')")` at class level
   - **Reason:** Enforce role-based access control at API level

3. **Frontend RBAC Implementation**
   - Modified: `App.tsx`
     - Added `isAdmin` state variable
     - Updated `handleLoginSuccess()` to set admin status
     - Conditionally render "User Management" menu item
   - Modified: `VehicleManagement.tsx`
     - Added `isAdmin` check using `auth.hasRole('ROLE_ADMIN')`
     - Hide "Add Vehicle" button for non-admins
     - Hide Edit and Delete buttons for non-admins (View remains for all)
   - **Reason:** Provide role-based UI experience

4. **Documentation**
   - Created: `ROADMAP.md` - Complete project roadmap (Phases 1-6)
   - Created: `CORS_FIX.md` - Detailed CORS error and fix documentation
   - Created: `claude.md` - This file
   - Created: `.claude/` folder structure
   - **Reason:** Maintain context across sessions

**Files Changed:**
```
M  src/main/java/com/muvs/inspection_system/config/SecurityConfig.java
M  src/main/java/com/muvs/inspection_system/controller/AuthController.java
M  src/main/java/com/muvs/inspection_system/controller/VehicleController.java
M  src/main/java/com/muvs/inspection_system/controller/UserController.java
M  frontend/App.tsx
M  frontend/components/VehicleManagement.tsx
A  .claude/
A  claude.md
A  ROADMAP.md
A  CORS_FIX.md
```

**Next Steps:**
- Test RBAC with admin and user accounts
- Create account management endpoints (profile, password change)
- Build account management UI
- Implement audit log filtering

---

## 🐛 Known Issues & Fixes

### Issue: CORS Error with Wildcard Origins
**Error:** `IllegalArgumentException: When allowCredentials is true, allowedOrigins cannot contain the special value "*"`

**Root Cause:** Using `@CrossOrigin(origins = "*")` with JWT authentication

**Solution:** Use `allowedOriginPatterns` in `SecurityConfig.java`
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",
    "http://127.0.0.1:*"
));
```

**Documentation:** See `CORS_FIX.md` for full details

---

## 🔧 Configuration Files

### Backend (`application.properties`)
```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your-secret-key-here
jwt.access-token-expiration=900000    # 15 minutes
jwt.refresh-token-expiration=604800000 # 7 days

# Server
server.port=8080
```

### Frontend (`config.ts`)
```typescript
const config = {
  apiUrl: 'http://localhost:8080',  // Backend URL
  tokenRefreshInterval: 840000,     // 14 minutes (refresh before expiry)
};
```

---

## 🧪 Testing Guidelines

### Manual Testing Checklist

**Authentication:**
- [ ] Login with admin credentials
- [ ] Login with user credentials
- [ ] Token refresh works automatically
- [ ] Logout clears tokens and session

**RBAC - Admin User:**
- [ ] Can see "User Management" in sidebar
- [ ] Can see "Add Vehicle" button
- [ ] Can see Edit and Delete buttons
- [ ] Can successfully create/edit/delete vehicles
- [ ] Can view all users

**RBAC - Regular User:**
- [ ] Cannot see "User Management" in sidebar
- [ ] Cannot see "Add Vehicle" button
- [ ] Cannot see Edit and Delete buttons
- [ ] Can view vehicles (read-only)
- [ ] Gets 403 Forbidden when trying admin API endpoints

**Data Loading:**
- [ ] Dashboard shows correct vehicle counts
- [ ] Vehicle list loads all 30 demo vehicles
- [ ] Audit trail shows change history
- [ ] Recent activities display on dashboard

---

## 📚 Additional Documentation

- **`ROADMAP.md`** - Complete project roadmap with all phases and features
- **`INTEGRATION.md`** - Backend-frontend integration technical reference
- **`CORS_FIX.md`** - CORS configuration issue and resolution
- **`.claude/sessions/`** - Session-specific change logs

---

## 💡 Development Tips for Claude

1. **Always check `claude.md` first** - This file contains the current state of the project
2. **Update change log** - After making changes, update the "Change Log - Claude Sessions" section
3. **Reference existing docs** - Check `ROADMAP.md`, `INTEGRATION.md`, `CORS_FIX.md` for detailed info
4. **Test credentials** - Use `admin/admin` for full access, `user/user` for read-only
5. **Backend running?** - Check `http://localhost:8080/h2-console` for database state
6. **Frontend running?** - Check browser console for API errors or CORS issues
7. **RBAC testing** - Always test with both user types after changes
8. **Git status** - Run `git status` to see current branch and changes

---

## 🎯 Current Sprint Goals

**Phase 4 - Testing & QA:**
1. ⏳ Set up testing infrastructure (dependencies, configuration)
2. ⏳ Write backend unit tests (service layer priority)
3. ⏳ Write frontend component tests
4. ⏳ Create integration tests for critical flows
5. ⏳ Achieve minimum test coverage (70% backend, 60% frontend)

**Success Criteria:**
- All critical services have unit tests
- Authentication and RBAC flows are tested
- Main UI components have tests
- Tests pass in CI/CD pipeline
- Code coverage meets minimum thresholds

---

**End of Claude Context Guide**
**Version:** 1.0.0
**Maintained by:** Claude Code Assistant
**Last Session:** 2025-11-23
