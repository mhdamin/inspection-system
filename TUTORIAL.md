# MUVS Inspection System - Tutorial

This tutorial provides instructions on how to run the MUVS Inspection System application and its tests.

## 1. Project Structure and Technologies

The project is a Spring Boot application that uses:

*   **Language:** Java 17
*   **Build Tool:** Maven
*   **Frameworks:** Spring Boot (Web, Data JPA, Security)
*   **Database:** H2 (in-memory)
*   **ORM:** Hibernate
*   **Utilities:** Lombok, MapStruct (though not explicitly used in the entities I modified, it's common in Spring projects for DTO mapping)
*   **Cloud Integration:** AWS S3 (for image storage, though currently mocked/disabled due to Docker issues)

## 2. Running the Application

### Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven (usually comes with your IDE, or can be installed separately)
*   A text editor or IDE (e.g., IntelliJ IDEA, VS Code)

### Steps

1.  **Open the project:** Open the `muvs-inspection-system` project in your preferred IDE.
2.  **Clean and Install Dependencies:**
    Open a terminal in the project's root directory and run:
    ```bash
    mvn clean install
    ```
    This command compiles the code, runs tests, and packages the application.

3.  **Run the Spring Boot application:**
    In the same terminal, run:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` by default.

    **Note:** Currently, the application is configured to use an in-memory H2 database. This means all data will be lost when the application stops. For persistent data, you would need to set up a proper database (like SQL Server, as originally planned with `docker-compose.yml`) and configure `application.yml` accordingly.

### Accessing the H2 Console (for in-memory database)

If you're running with the H2 in-memory database (as currently configured):

1.  While the application is running, open your web browser.
2.  Navigate to `http://localhost:8080/h2-console`.
3.  Enter the JDBC URL: `jdbc:h2:mem:testdb`
4.  Enter the Username: `sa`
5.  Leave the Password field empty.
6.  Click "Connect".

You should now see the H2 console, allowing you to browse the in-memory database tables.

## 3. Running Tests

### Prerequisites

*   The same prerequisites as running the application.

### Steps

1.  **Run all tests:**
    Open a terminal in the project's root directory and run:
    ```bash
    mvn test
    ```
    This command will execute all unit and integration tests in the project.

2.  **Run a specific test class:**
    To run a specific test class (e.g., `SubChecklistServiceImplTest`), use:
    ```bash
    mvn test -Dtest=SubChecklistServiceImplTest
    ```

## 4. Where is the PRD file?

The Product Requirements Document (PRD) is located in the root directory of the project, named `SUMMARY.md`. You can open it with any text editor or markdown viewer.
