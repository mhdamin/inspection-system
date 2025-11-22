# MUVS Inspection System

This document provides a summary of the MUVS Inspection System application, including its structure, technology stack, and suggestions for future features. It also includes a Product Requirements Document (PRD).

## Application Structure

The application is a Spring Boot-based RESTful API for a vehicle inspection system. It follows a standard three-tier architecture:

*   **Controller Layer:** Handles incoming HTTP requests and delegates them to the service layer.
*   **Service Layer:** Contains the business logic of the application.
*   **Repository Layer:** Interacts with the database to perform CRUD operations.

The main entities in the application are:

*   `Vehicle`: Represents a vehicle.
*   `Checklist`: Represents an inspection checklist for a vehicle.
*   `SubChecklist`: Represents a sub-section of a checklist.
*   `Defect`: Represents a defect found during an inspection.
*   `DefectImage`: Represents an image of a defect.
*   `Signature`: Represents a signature on a checklist.
*   `VehicleChangeLog`: Represents a log of changes made to a vehicle.

## Technology Stack

*   **Backend:**
    *   Java 17
    *   Spring Boot 3.5.7
    *   Spring Web
    *   Spring Data JPA
    *   Spring Security
    *   Hibernate
    *   Lombok
*   **Database:**
    *   H2 (in-memory)
*   **Cloud:**
    *   AWS S3 for image storage
*   **Build Tool:**
    *   Maven

## Future Features Suggestion

*   **User Management:** Implement user authentication and authorization to control access to the API.
*   **Reporting:** Generate PDF reports of inspection checklists.
*   **Analytics:** Provide analytics on inspection data, such as the most common defects.
*   **Real-time Notifications:** Send real-time notifications to users when a new defect is found.
*   **Integration with other systems:** Integrate with other systems, such as a fleet management system.

## Product Requirements Document (PRD)

### 1. Introduction

This PRD outlines the requirements for the MUVS Inspection System. The system is a web-based application that allows users to create, manage, and track vehicle inspections.

### 2. Goals

The primary goals of the MUVS Inspection System are to:

*   Streamline the vehicle inspection process.
*   Improve the accuracy and consistency of inspection data.
*   Provide a centralized repository for all inspection data.
*   Reduce the time and effort required to conduct inspections.

### 3. User Stories

*   As a technician, I want to be able to create a new inspection checklist for a vehicle so that I can record the results of my inspection.
*   As a technician, I want to be able to add defects to a checklist so that I can document any problems that I find.
*   As a technician, I want to be able to upload images of defects so that I can provide visual evidence of the problem.
*   As a technician, I want to be able to sign a checklist so that I can certify that I have completed the inspection.
*   As a manager, I want to be able to view all inspection checklists so that I can track the status of inspections.
*   As a manager, I want to be able to generate reports on inspection data so that I can identify trends and patterns.

### 4. Functional Requirements

#### 4.1. Vehicle Management

*   The system shall allow users to create, read, update, and delete vehicles.
*   Each vehicle shall have a unique vehicle identification number (VIN).

#### 4.2. Checklist Management

*   The system shall allow users to create, read, update, and delete inspection checklists.
*   Each checklist shall be associated with a vehicle.
*   Each checklist shall have a unique checklist number.

#### 4.3. Defect Management

*   The system shall allow users to add defects to a checklist.
*   Each defect shall have a name, description, and type.
*   Each defect shall be associated with a sub-checklist.

#### 4.4. Image Management

*   The system shall allow users to upload images of defects.
*   Each image shall be associated with a defect.
*   Images shall be stored in AWS S3.

#### 4.5. Signature Management

*   The system shall allow users to sign a checklist.
*   Each signature shall be associated with a checklist.
*   Signatures shall be stored as images in AWS S3.

### 5. Non-Functional Requirements

*   **Security:** The system shall be secure and protect user data.
*   **Performance:** The system shall be responsive and perform well under load.
*   **Scalability:** The system shall be scalable and able to handle a large number of users and inspections.
*   **Reliability:** The system shall be reliable and available 24/7.
