# Phase 4 - Testing & Quality Assurance Plan

**Created:** 2025-11-24
**Status:** In Progress
**Target Coverage:** Backend 70%, Frontend 60%

---

## 📋 Overview

This document outlines the comprehensive testing strategy for the MUVS Inspection System. Our goal is to ensure reliability, security, and maintainability through automated testing at multiple levels.

---

## 🎯 Testing Goals

### Primary Objectives
1. Ensure all critical business logic is tested
2. Verify authentication and authorization work correctly
3. Prevent regressions in future development
4. Achieve minimum code coverage thresholds
5. Build confidence for production deployment

### Success Metrics
- ✅ Backend test coverage: ≥70%
- ✅ Frontend test coverage: ≥60%
- ✅ All tests pass in CI/CD pipeline
- ✅ Zero critical bugs in authentication/security flows
- ✅ All API endpoints have integration tests

---

## 🔧 Testing Infrastructure

### Backend Testing Stack

**Frameworks & Libraries:**
- ✅ **JUnit 5** - Test framework (included in spring-boot-starter-test)
- ✅ **Mockito** - Mocking framework (included in spring-boot-starter-test)
- ✅ **Spring Boot Test** - Testing utilities (included)
- ✅ **MockMvc** - API testing (included)
- ⏳ **JaCoCo** - Code coverage reporting (to add)
- ⏳ **Spring Security Test** - Security testing (to add)

**Dependencies to Add:**
```xml
<!-- Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>

<!-- Security Testing -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Frontend Testing Stack

**Frameworks & Libraries:**
- ⏳ **Vitest** - Fast unit test framework
- ⏳ **React Testing Library** - Component testing
- ⏳ **@testing-library/user-event** - User interaction simulation
- ⏳ **jsdom** - DOM environment for Node
- ⏳ **@vitest/coverage-v8** - Code coverage

**Dependencies to Add:**
```json
{
  "devDependencies": {
    "vitest": "^1.0.0",
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.1.0",
    "@testing-library/user-event": "^14.5.0",
    "jsdom": "^23.0.0",
    "@vitest/coverage-v8": "^1.0.0"
  }
}
```

---

## 📁 Test Directory Structure

### Backend Test Structure
```
src/test/java/com/muvs/inspection_system/
├── service/
│   ├── impl/
│   │   ├── VehicleServiceImplTest.java
│   │   ├── UserServiceImplTest.java
│   │   ├── RefreshTokenServiceImplTest.java
│   │   ├── VehicleChangeLogServiceImplTest.java
│   │   └── SubChecklistServiceImplTest.java
│   └── integration/
│       └── VehicleServiceIntegrationTest.java
├── controller/
│   ├── AuthControllerTest.java
│   ├── VehicleControllerTest.java
│   ├── UserControllerTest.java
│   └── VehicleChangeLogControllerTest.java
├── security/
│   ├── JwtUtilTest.java
│   └── JwtAuthenticationFilterTest.java
├── config/
│   └── SecurityConfigTest.java
└── TestDataFactory.java  # Test data builder utility
```

### Frontend Test Structure
```
frontend/
├── __tests__/
│   ├── components/
│   │   ├── Login.test.tsx
│   │   ├── Dashboard.test.tsx
│   │   ├── VehicleManagement.test.tsx
│   │   ├── UserManagement.test.tsx
│   │   └── AuditTrail.test.tsx
│   ├── utils/
│   │   └── config.test.ts
│   └── setup.ts
└── vitest.config.ts
```

---

## 🧪 Testing Strategy by Layer

### 1. Backend Unit Tests

#### Service Layer Tests (PRIORITY: HIGH)

**VehicleServiceImplTest.java**
```java
@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleChangeLogService changeLogService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void createVehicle_ValidData_ReturnsCreatedVehicle()

    @Test
    void updateVehicle_ExistingVehicle_UpdatesAndLogsChange()

    @Test
    void deleteVehicle_ExistingVehicle_DeletesSuccessfully()

    @Test
    void getVehicleById_NonExistent_ThrowsException()

    @Test
    void getVehicleStats_MultipleVehicles_ReturnsCorrectCounts()
}
```

**UserServiceImplTest.java**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ValidData_ReturnsCreatedUser()

    @Test
    void createUser_DuplicateUsername_ThrowsException()

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails()

    @Test
    void loadUserByUsername_NonExistent_ThrowsException()
}
```

**RefreshTokenServiceImplTest.java**
```java
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void createRefreshToken_ValidUser_ReturnsToken()

    @Test
    void verifyExpiration_ExpiredToken_ThrowsException()

    @Test
    void verifyExpiration_ValidToken_ReturnsToken()

    @Test
    void deleteByUserId_ExistingUser_DeletesTokens()
}
```

#### Controller Layer Tests (PRIORITY: MEDIUM)

**AuthControllerTest.java**
```java
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Test
    void login_ValidCredentials_ReturnsTokens() throws Exception

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception

    @Test
    void refreshToken_ValidToken_ReturnsNewAccessToken() throws Exception

    @Test
    void logout_AuthenticatedUser_RevokesToken() throws Exception
}
```

**VehicleControllerTest.java**
```java
@WebMvcTest(VehicleController.class)
@Import(SecurityConfig.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_AsAdmin_Returns201() throws Exception

    @Test
    @WithMockUser(roles = "USER")
    void createVehicle_AsUser_Returns403() throws Exception

    @Test
    @WithMockUser
    void getAllVehicles_Authenticated_Returns200() throws Exception

    @Test
    void getAllVehicles_Unauthenticated_Returns401() throws Exception
}
```

#### Security Component Tests (PRIORITY: HIGH)

**JwtUtilTest.java**
```java
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "test-secret-key-for-testing-purposes");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 900000L);
    }

    @Test
    void generateToken_ValidUser_ReturnsToken()

    @Test
    void extractUsername_ValidToken_ReturnsUsername()

    @Test
    void validateToken_ValidToken_ReturnsTrue()

    @Test
    void validateToken_ExpiredToken_ReturnsFalse()

    @Test
    void validateToken_MalformedToken_ReturnsFalse()
}
```

### 2. Backend Integration Tests

**AuthenticationFlowIntegrationTest.java**
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completeAuthFlow_LoginRefreshLogout_Success() throws Exception {
        // 1. Login with valid credentials
        // 2. Access protected endpoint with token
        // 3. Refresh token
        // 4. Access protected endpoint with new token
        // 5. Logout
        // 6. Verify token is revoked
    }

    @Test
    void rbacFlow_AdminAndUserAccess_CorrectPermissions() throws Exception
}
```

### 3. Frontend Unit Tests

#### Component Tests

**Login.test.tsx**
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Login from '../components/Login';

describe('Login Component', () => {
  it('renders login form', () => {
    render(<Login onLoginSuccess={vi.fn()} />);
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  });

  it('submits form with valid credentials', async () => {
    const mockOnLoginSuccess = vi.fn();
    render(<Login onLoginSuccess={mockOnLoginSuccess} />);

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: 'admin' }
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: 'admin' }
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(mockOnLoginSuccess).toHaveBeenCalled();
    });
  });

  it('displays error message on failed login', async () => {
    // Test error handling
  });
});
```

**Dashboard.test.tsx**
```typescript
import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Dashboard from '../components/Dashboard';

describe('Dashboard Component', () => {
  it('displays vehicle statistics', async () => {
    render(<Dashboard />);
    await waitFor(() => {
      expect(screen.getByText(/total vehicles/i)).toBeInTheDocument();
    });
  });

  it('shows loading state while fetching data', () => {
    render(<Dashboard />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });
});
```

#### Utility Tests

**config.test.ts**
```typescript
import { describe, it, expect } from 'vitest';
import { auth } from '../config';

describe('Auth Utilities', () => {
  it('isAuthenticated returns true when token exists', () => {
    localStorage.setItem('accessToken', 'test-token');
    expect(auth.isAuthenticated()).toBe(true);
  });

  it('hasRole returns true for valid role', () => {
    localStorage.setItem('userRoles', JSON.stringify(['ROLE_ADMIN']));
    expect(auth.hasRole('ROLE_ADMIN')).toBe(true);
  });

  it('logout clears all tokens', () => {
    localStorage.setItem('accessToken', 'test-token');
    localStorage.setItem('refreshToken', 'test-refresh');
    auth.logout();
    expect(localStorage.getItem('accessToken')).toBeNull();
  });
});
```

---

## 📊 Code Coverage Configuration

### Backend - JaCoCo Configuration

**pom.xml**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Frontend - Vitest Coverage

**vitest.config.ts**
```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './__tests__/setup.ts',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        '__tests__/',
        '*.config.ts',
      ],
      lines: 60,
      functions: 60,
      branches: 60,
      statements: 60,
    },
  },
});
```

---

## 🚀 Implementation Roadmap

### Phase 4.1: Setup & Infrastructure (Day 1)
- [x] Create testing plan document
- [ ] Add JaCoCo plugin to pom.xml
- [ ] Add Spring Security Test dependency
- [ ] Install Vitest and React Testing Library
- [ ] Configure vitest.config.ts
- [ ] Create test directory structure
- [ ] Create TestDataFactory utility class

### Phase 4.2: Critical Backend Tests (Days 2-3)
- [ ] VehicleServiceImplTest (complete coverage)
- [ ] UserServiceImplTest (complete coverage)
- [ ] RefreshTokenServiceImplTest (complete coverage)
- [ ] JwtUtilTest (complete coverage)
- [ ] Run coverage report and verify ≥70%

### Phase 4.3: Backend Controller Tests (Day 4)
- [ ] AuthControllerTest (login, register, refresh, logout)
- [ ] VehicleControllerTest (CRUD + RBAC)
- [ ] UserControllerTest (RBAC)
- [ ] VehicleChangeLogControllerTest

### Phase 4.4: Frontend Component Tests (Days 5-6)
- [ ] Login.test.tsx
- [ ] Dashboard.test.tsx
- [ ] VehicleManagement.test.tsx
- [ ] UserManagement.test.tsx
- [ ] config.test.ts (auth utilities)
- [ ] Run coverage report and verify ≥60%

### Phase 4.5: Integration Tests (Day 7)
- [ ] AuthenticationFlowIntegrationTest
- [ ] RBACIntegrationTest
- [ ] VehicleOperationsIntegrationTest

### Phase 4.6: CI/CD Integration (Day 8)
- [ ] Configure GitHub Actions workflow
- [ ] Add test execution step
- [ ] Add coverage reporting
- [ ] Configure test failure gates

---

## 🎯 Test Execution Commands

### Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=VehicleServiceImplTest

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run tests and skip if needed
mvn clean install -DskipTests
```

### Frontend Tests

```bash
# Run all tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run specific test file
npm run test Login.test.tsx
```

**Add to package.json:**
```json
{
  "scripts": {
    "test": "vitest run",
    "test:watch": "vitest",
    "test:coverage": "vitest run --coverage",
    "test:ui": "vitest --ui"
  }
}
```

---

## ✅ Testing Checklist

### Before Committing Code
- [ ] All tests pass locally
- [ ] New features have corresponding tests
- [ ] Test coverage meets minimum thresholds
- [ ] No console errors or warnings in tests
- [ ] Tests are deterministic (no flaky tests)

### Before Merging to Main
- [ ] All tests pass in CI/CD pipeline
- [ ] Code coverage report reviewed
- [ ] No regressions in existing functionality
- [ ] Integration tests pass
- [ ] Manual testing completed for UI changes

---

## 📚 Best Practices

### General Testing Principles
1. **Arrange-Act-Assert (AAA)** - Structure all tests clearly
2. **Single Responsibility** - One assertion per test (when possible)
3. **Descriptive Names** - Test names should describe the scenario
4. **Test Isolation** - Tests should not depend on each other
5. **Mock External Dependencies** - Database, API calls, etc.

### Backend Testing Best Practices
- Use `@ExtendWith(MockitoExtension.class)` for unit tests
- Use `@SpringBootTest` sparingly (slower than unit tests)
- Mock repositories and external services
- Use `@WebMvcTest` for controller tests
- Test both success and failure scenarios
- Verify exception handling

### Frontend Testing Best Practices
- Test user behavior, not implementation details
- Use `screen.getByRole` over `getByTestId`
- Mock API calls with MSW or vi.fn()
- Test accessibility (screen readers, keyboard navigation)
- Avoid testing library internals
- Focus on critical user paths

---

## 🐛 Common Testing Pitfalls to Avoid

1. ❌ Testing implementation details instead of behavior
2. ❌ Not cleaning up after tests (memory leaks)
3. ❌ Hardcoding dates/times (use mocks)
4. ❌ Overmocking (mocking too much loses confidence)
5. ❌ Ignoring async operations (use waitFor)
6. ❌ Not testing edge cases
7. ❌ Writing tests that pass when they should fail

---

## 📈 Success Metrics

### Minimum Requirements
- ✅ Backend coverage: ≥70%
- ✅ Frontend coverage: ≥60%
- ✅ All critical paths tested
- ✅ Zero failing tests in CI/CD
- ✅ Authentication flow fully tested
- ✅ RBAC permissions verified

### Stretch Goals
- 🎯 Backend coverage: 80%+
- 🎯 Frontend coverage: 70%+
- 🎯 E2E tests implemented
- 🎯 Performance tests added
- 🎯 Security vulnerability tests

---

**Last Updated:** 2025-11-24
**Status:** Infrastructure setup in progress
**Next Action:** Add testing dependencies and create test directory structure
