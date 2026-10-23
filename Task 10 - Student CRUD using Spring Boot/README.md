# Task 10: Student CRUD using Spring Boot

## Overview
A production-ready RESTful CRUD application built with **Spring Boot** and **Spring Data JPA**. Demonstrates automatic configuration, embedded servlet containers, entity mapping with Hibernate ORM, and comprehensive persistence operations against MySQL.

---

## Architectural Layers

```
+------------------------------------+
|    REST Controller (@RestController)|  Handles HTTP requests & returns JSON responses
+------------------------------------+
                 |
                 v
+------------------------------------+
|       Service Layer (@Service)     |  Encapsulates transaction boundaries & business logic
+------------------------------------+
                 |
                 v
+------------------------------------+
|   Repository (JpaRepository<T, ID>)|  Spring Data JPA abstractions for SQL CRUD
+------------------------------------+
                 |
                 v
+------------------------------------+
|     Relational Database (MySQL)    |  Table: students
+------------------------------------+
```

---

## REST Endpoints Specification

| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `GET` | `/students` | Retrieve all student records | `200 OK` |
| `GET` | `/students/{id}` | Retrieve single student by primary key | `200 OK` / `404 Not Found` |
| `POST` | `/students` | Create new student | `201 Created` |
| `PUT` | `/students/{id}` | Update existing student record | `200 OK` |
| `DELETE` | `/students/{id}` | Delete student by ID | `204 No Content` |

---

## Sample Request Payloads

### Create Student (`POST /students`)
```json
{
  "name": "Edison Hithesh",
  "email": "edison@university.edu",
  "department": "Computer Science",
  "phone": "+91-9876543210",
  "dob": "2002-11-20"
}
```

---

## Execution Guide

1. **Verify MySQL Credentials**:
   Ensure MySQL is running and `application.properties` reflects your local database:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/studenthub?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **Run Spring Boot Application**:
   ```powershell
   cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 10 - Student CRUD using Spring Boot"
   $env:JAVA_HOME="C:\Program Files\Java\jdk-23"
   $env:Path="$env:JAVA_HOME\bin;$env:Path"
   mvn spring-boot:run
   ```

3. **Verify API Endpoints**:
   - Navigate to `http://localhost:8080/students` in browser or run:
     ```powershell
     Invoke-RestMethod -Uri "http://localhost:8080/students" -Method Get
     ```
