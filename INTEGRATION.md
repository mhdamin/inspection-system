# Frontend-Backend Integration Summary

This document summarizes the integration of the frontend components with the backend API.

## Integration Summary

The following components have been integrated with the backend:

*   **Dashboard:**
    *   Fetches vehicle statistics from `/api/vehicles/stats`.
    *   Fetches recent activities from `/api/activities`.
*   **Vehicle Management:**
    *   Fetches all vehicles from `/api/vehicles`.
*   **User Management:**
    *   Fetches all users from `/api/users`.
*   **Audit Trail:**
    *   Fetches all activities from `/api/activities`.
*   **Checklist Management:**
    *   Fetches vehicle data from `/api/vehicles/{id}` when a vehicle ID is entered.

## How to Run the Application

To run the full integrated application:

### Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven (usually comes with your IDE, or can be installed separately)
*   Node.js and npm (or Yarn)
*   A text editor or IDE (e.g., IntelliJ IDEA, VS Code)

### Steps to Run Backend

1.  Open a terminal in the project's root directory (`muvs-inspection-system`).
2.  Run the backend application:
    ```bash
    mvn spring-boot:run
    ```
    The backend will start on `http://localhost:8080`.

### Steps to Run Frontend

1.  Open a **new terminal** and navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install frontend dependencies (if you haven't already):
    ```bash
    npm install
    ```
3.  Start the frontend development server:
    ```bash
    npm run dev
    ```
    The frontend application will typically open in your browser at `http://localhost:3000`.
