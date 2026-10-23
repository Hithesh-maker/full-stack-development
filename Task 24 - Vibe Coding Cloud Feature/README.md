# Task 24: Vibe Coding Cloud Feature: Cloud Notification & Audit Event Dispatcher

## Overview
A complete cloud-native microservice built through the iterative **Vibe Coding** process using Generative AI. Features a secured RESTful event ingestion and dispatch API (`/api/v1/notifications`), Zero-Trust `X-API-KEY` security filtering, Jakarta Bean Validation, in-memory/cloud persistence, multi-stage distroless containerization (`Dockerfile`), Docker Compose orchestration, and Kubernetes production manifests with health probes.

---

## Architectural Topology

```
[ External Cloud Clients / Microservices ]
                   |
                   | HTTP POST /api/v1/notifications
                   | Header: X-API-KEY: vibe-cloud-secret-key-2026
                   v
+-------------------------------------------------------+
|             Cloud Notification Dispatcher             |
|                      (Port: 8086)                     |
|                                                       |
|   +-----------------------------------------------+   |
|   | ApiKeySecurityFilter (Returns 401 if missing) |   |
|   +-----------------------------------------------+   |
|                          |                            |
|                          v                            |
|   +-----------------------------------------------+   |
|   | NotificationController & Service              |   |
|   +-----------------------------------------------+   |
|                          |                            |
|                          v                            |
|   +-----------------------------------------------+   |
|   | Spring Data JPA (NotificationRepository)      |   |
|   +-----------------------------------------------+   |
+-------------------------------------------------------+
       |                                       |
       v (Cloud Event Dispatch)                v (Orchestration & Health)
[ AWS SNS / GCP Pub/Sub / Email ]   [ Kubernetes Liveness & Readiness Probes ]
```

---

## Prompt Evolution Journey

1. **Iteration 1 (Naive Prompt)**: Basic unauthenticated prototype lacking validation, tests, or containers (Score: 42/100).
2. **Iteration 2 (Architectural Refinement)**: Added validation annotations, status codes, and basic Dockerfile (Score: 74/100).
3. **Iteration 3 (Production Vibe Master Prompt)**: Enforced Zero-Trust API Key headers, multi-stage JRE container, Kubernetes manifests, and full JUnit test coverage (Score: **98/100**).

*For the complete prompt text and comparative analysis, see [`PROMPT_ITERATION_JOURNAL.md`](file:///C:/Games/Project/Full%20Stack%20Development/full-stack-development/Task%2024%20-%20Vibe%20Coding%20Cloud%20Feature/PROMPT_ITERATION_JOURNAL.md).*

---

## REST Endpoints Specification

| Method | Endpoint | Security Header | Description |
|---|---|---|---|
| `POST` | `/api/v1/notifications` | `X-API-KEY` required | Ingests and dispatches alert/notification event (`201 Created`) |
| `GET` | `/api/v1/notifications` | `X-API-KEY` required | Query all notifications; supports `?priority=HIGH` filter |
| `GET` | `/api/v1/notifications/{id}` | `X-API-KEY` required | Fetch single notification details |
| `GET` | `/actuator/health` | Public (K8s probe) | Readiness and Liveness health probe check |

---

## Automated Execution & Test Verification

```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 24 - Vibe Coding Cloud Feature"
mvn clean test
```

### Verified Sample Output
```text
[INFO] Running com.cloud.notification.NotificationServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Local API Testing with PowerShell

```powershell
# 1. Start Service
mvn spring-boot:run

# 2. Test Authorized Notification Dispatch
$headers = @{
    "Content-Type" = "application/json"
    "X-API-KEY" = "vibe-cloud-secret-key-2026"
}

$body = @{
    recipient = "student@university.edu"
    subject = "Exam Schedule Published"
    messageBody = "The final exam timetable for Semester 5 is now accessible."
    priority = "HIGH"
    channel = "EMAIL"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8086/api/v1/notifications" -Method Post -Headers $headers -Body $body
```
