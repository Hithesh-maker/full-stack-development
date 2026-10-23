# Task 8: Employee Management using Spring Core

## Overview
A foundational Spring enterprise application demonstrating the core tenets of the Spring Framework: **Inversion of Control (IoC)**, **Dependency Injection (DI)**, and **Bean Lifecycle Management** using Java-based configuration (`@Configuration`, `@Bean`, `@ComponentScan`).

---

## Architectural Principles

```
+-------------------------------------------------------+
|                 Spring IoC Container                  |
|                 (AnnotationConfigApplicationContext)  |
|                                                       |
|   +---------------------+      Injects (@Autowired)   |
|   | EmployeeRepository  | ------------------------->   |
|   +---------------------+                             |
|                                     v                 |
|                             +-----------------+       |
|                             | EmployeeService |       |
|                             +-----------------+       |
+-------------------------------------------------------+
                           |
                           v
                     [Main Client]
```

- **Inversion of Control (IoC)**: The lifecycle and instantiations of repositories and services are shifted from caller-managed `new` keywords to the Spring IoC container.
- **Dependency Injection (DI)**: Constructor-based injection ensures that `EmployeeService` receives an immutable instance of `EmployeeRepository` without hardcoding dependencies.

---

## Component Architecture

1. **Model** (`Employee.java`): POJO representing employee attributes (`id`, `name`, `department`, `salary`).
2. **Repository** (`EmployeeRepository.java`): Simulates persistent data store operations with in-memory storage.
3. **Service** (`EmployeeService.java`): Encapsulates business logic, calculating net compensation and filtering employees.
4. **Configuration** (`AppConfig.java`): Spring Java configuration registering beans and component scan directives.
5. **Driver** (`Main.java`): Initializes `AnnotationConfigApplicationContext` and retrieves managed beans.

---

## Execution Guide

### 1. Build and Compile
```bash
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 8 - Employee Management using Spring Core"
mvn clean compile
```

### 2. Run Application
```bash
mvn exec:java "-Dexec.mainClass=com.employee.Main"
```

*Expected Console Output*:
```text
=== Spring Core Employee Management System ===
Employee added: ID=101, Name=John Doe, Dept=Engineering, Salary=75000.0
Employee added: ID=102, Name=Jane Smith, Dept=Marketing, Salary=68000.0
--- Employee Directory ---
[Employee{id=101, name='John Doe', department='Engineering', salary=75000.0}, ...]
```
