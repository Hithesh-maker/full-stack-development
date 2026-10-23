# Full Stack Engineering & Cloud Architecture Portfolio

![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-23-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green.svg)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)
![Node.js](https://img.shields.io/badge/Node.js-20.x-green.svg)
![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-blue.svg)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions%20%7C%20Jenkins-blueviolet.svg)
![Cloud](https://img.shields.io/badge/Cloud-AWS%20%7C%20GCP%20%7C%20Azure-yellow.svg)

A comprehensive, production-grade full-stack engineering curriculum and distributed systems portfolio. Spans database persistence and event-driven front-ends (Node.js, Express, MySQL), enterprise Java development (Spring Core, Spring MVC, Spring Boot, Spring Data JPA), distributed microservices (Eureka, API Gateway, Circuit Breakers, Fault Tolerance), automated software testing, DevOps & CI/CD automation, cloud infrastructure comparison, and Generative AI prompt engineering (Vibe Coding).

---

## High-Level Architecture Map

```
                                  [ Internet / Client Devices ]
                                                |
                                                v
                        +-----------------------------------------------+
                        |       Task 16: Spring Cloud API Gateway       |
                        |                 (Port: 8080)                  |
                        | - Client-side Round Robin Load Balancing      |
                        | - Global Request/Response Auditing Filter     |
                        +-----------------------------------------------+
                                                |
                 +------------------------------+------------------------------+
                 | (Resolves Service IDs via Task 15 Eureka Discovery Server)   |
                 v                                                              v
+------------------------------------+                        +------------------------------------+
|   Task 14/18: Product Service      |                        | Task 14/17: Resilient Order Svc    |
|       (Ports: 8081, 8083)          | <--- Inter-Service --- |            (Port: 8084)            |
| - REST Catalog Management API      |      REST + Feign      | - Resilience4j Circuit Breaker     |
| - Spring Data JPA & MySQL          |                        | - Graceful Service Degradation     |
+------------------------------------+                        +------------------------------------+
                 |                                                              |
                 v                                                              v
        [ MySQL Product DB ]                                           [ MySQL Order DB ]

====================================================================================================
  Core Web & SQL Foundation (Tasks 1 to 7)     | Enterprise Java Core (Tasks 8 to 13)
  - Task 1: Student Registration (MySQL INSERT) | - Task 8: Employee Management (Spring Core IoC)
  - Task 2: Data Retrieval & Sorter Dashboard  | - Task 9: Employee Details (Spring MVC & Tomcat)
  - Task 3: Login Authentication & Validation  | - Task 10: Student CRUD API (Spring Boot & JPA)
  - Task 4: Order Management (Multi-Table Join)| - Task 11: Advanced Spring Data JPA & Pagination
  - Task 5: Payment Simulator (ACID Transaction)| - Task 12: Product Management RESTful Web Service
  - Task 6: Audit Triggers & Reporting Views   | - Task 13: Centralized Exception Handling (@Valid)
  - Task 7: Reactive Events & Interactive Form |
====================================================================================================
  DevOps, Cloud & AI Engineering (Tasks 19 to 24)
  - Task 19: Git Version Control Fundamentals & Remote Management
  - Task 20: Git Branching Strategies, 3-Way Merging, Rebasing & Conflict Resolution
  - Task 21: Automated Multi-Platform CI/CD Pipelines (GitHub Actions, Jenkins, GitLab CI)
  - Task 22: Cloud Service Providers Comparative Study (AWS vs. GCP vs. Azure)
  - Task 23: Vibe Coding & Systematic Prompt Engineering Laboratory
  - Task 24: Vibe Coded Cloud Feature: Cloud Notification & Audit Event Dispatcher
```

---

## Complete Curriculum Task Directory

### Phase 1: Web Systems & Relational Database Engineering
* [Task 1: Student Registration & Data Storage](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task-1-Student-Registration/README.md)
* [Task 2: Data Retrieval & Sorting Dashboard](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%202%20-%20Data%20Retrieval%20%26%20Sorting%20Dashboard/README.md)
* [Task 3: Login System with Validation](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%203%20-%20Login%20System%20with%20Validation/README.md)
* [Task 4: Order Management using SQL Joins](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%204%20-%20Order%20Management%20using%20Joins/README.md)
* [Task 5: Transaction-Based Payment Simulation (ACID Transactions)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%205%20-%20Transaction%20Based%20Payment%20Simulation/README.md)
* [Task 6: Automated Logging using Database Triggers & Views](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%206%20-%20Automated%20Logging%20using%20Triggers%20%26%20Views/README.md)
* [Task 7: Interactive Web Form with Events & Functions](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%207%20-%20Interactive%20Web%20Form%20with%20Events%20%26%20Functions/README.md)

### Phase 2: Enterprise Java & Spring Ecosystem
* [Task 8: Employee Management using Spring Core (IoC & DI)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%208%20-%20Employee%20Management%20using%20Spring%20Core/README.md)
* [Task 9: Spring MVC Employee Details (DispatcherServlet & JSTL)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%209%20-%20Spring%20MVC%20Employee%20Details/README.md)
* [Task 10: Student CRUD using Spring Boot & Hibernate](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2010%20-%20Student%20CRUD%20using%20Spring%20Boot/README.md)
* [Task 11: Advanced Spring Data JPA (Derived Queries & Pagination)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2011%20-%20Spring%20Data%20JPA/README.md)
* [Task 12: Product Management RESTful Web Service](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2012%20-%20Product%20Management%20REST%20API/README.md)
* [Task 13: Centralized Exception Handling & Jakarta Bean Validation](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2013%20-%20Exception%20Handling%20and%20Validation/README.md)

### Phase 3: Distributed Microservices Architecture & Testing
* [Task 14: Microservices Architecture (Product & Order Services)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2014%20-%20Microservices/README.md)
* [Task 15: Service Discovery with Spring Cloud Netflix Eureka](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2015%20-%20Service%20Discovery/README.md)
* [Task 16: Spring Cloud API Gateway & Client-Side Load Balancing](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2016%20-%20API%20Gateway%20and%20Load%20Balancing/README.md)
* [Task 17: Resilient Inter-Service REST Communication (Resilience4j)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2017%20-%20Resilient%20Inter-Service%20Communication/README.md)
* [Task 18: Microservices Unit & Slice Testing Suite (JUnit 5, Mockito, MockMvc)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2018%20-%20Microservices%20Unit%20Testing/README.md)

### Phase 4: DevOps, Cloud Computing & Generative AI Engineering
* [Task 19: Git Version Control Fundamentals & Remote Management](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2019%20-%20Git%20Version%20Control%20Operations/README.md)
* [Task 20: Git Branching Strategies, Rebasing & Conflict Resolution](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2020%20-%20Git%20Branching%20Strategies%20and%20Conflict%20Resolution/README.md)
* [Task 21: Automated Multi-Platform CI/CD Pipeline (GitHub Actions, Jenkins, GitLab)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2021%20-%20CI-CD%20Pipeline%20Implementation/README.md)
* [Task 22: Cloud Service Providers Comparative Study (AWS, GCP, Azure)](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2022%20-%20Cloud%20Service%20Providers%20Comparative%20Study/README.md)
* [Task 23: Vibe Coding & Systematic Prompt Engineering Framework](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2023%20-%20Vibe%20Coding%20and%20Prompt%20Engineering/README.md)
* [Task 24: Vibe Coded Cloud Feature: Notification & Audit Dispatcher](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2024%20-%20Vibe%20Coding%20Cloud%20Feature/README.md)

---

## Quickstart & Verification

### 1. Run Node.js & MySQL Central Backend (Tasks 1 to 7)
```bash
npm install
node server.js
```

### 2. Run Local CI/CD Pipeline Simulator
```powershell
powershell -ExecutionPolicy Bypass -File "Task 21 - CI-CD Pipeline Implementation\test_ci_pipeline.ps1"
```

### 3. Run Automated Microservice Unit Tests (Task 18)
```powershell
cd "Task 18 - Microservices Unit Testing"
mvn clean test
```

### 4. Run Git Branching & Conflict Resolution Simulator (Task 20)
```powershell
powershell -ExecutionPolicy Bypass -File "Task 20 - Git Branching Strategies and Conflict Resolution\simulate_branching_and_conflicts.ps1"
```

### 5. Run Cloud Infrastructure Cost Estimator (Task 22)
```bash
python "Task 22 - Cloud Service Providers Comparative Study\cloud_cost_calculator.py"
```

### 6. Run Prompt Engineering Benchmark Evaluation (Task 23)
```bash
python "Task 23 - Vibe Coding and Prompt Engineering\evaluate_prompts.py"
```
