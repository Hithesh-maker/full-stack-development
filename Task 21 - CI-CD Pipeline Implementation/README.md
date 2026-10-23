# Task 21: Continuous Integration & Continuous Deployment (CI/CD) Pipeline

## Overview
An automated Continuous Integration and Continuous Deployment (CI/CD) infrastructure implemented across **GitHub Actions**, **Jenkins**, and **GitLab CI**. Automates every phase of the software delivery lifecycle from source code checkout, multi-language toolchain setup (JDK 23 + Node.js 20), automated unit & slice test execution, code quality checks, artifact packaging (JARs/containers), and automated deployment promotion to staging environments.

---

## CI/CD Pipeline Architecture

```
[ Push / Pull Request to main ]
              |
              v
+-------------------------------+
|  1. Checkout Source Code      | (actions/checkout@v4)
+-------------------------------+
              |
              +-----------------------------+
              |                             |
              v                             v
+-------------------------------+ +-------------------------------+
|  2. Node.js CI (Tasks 1-7)    | |  3. Spring Boot CI (14-18)    |
|     - Node 20 environment     | |     - JDK 23 Temurin          |
|     - npm ci                  | |     - Maven dependency cache  |
|     - Syntax & Lint checks    | |     - mvn clean test (JUnit 5)|
+-------------------------------+ +-------------------------------+
              |                             |
              +--------------+--------------+
                             | (Both Passed)
                             v
+-----------------------------------------------+
|  4. Package Deliverable Artifacts             |
|     - mvn clean package -DskipTests           |
|     - Archive executable Spring Boot JARs     |
|     - Generate Surefire XML Test Reports      |
+-----------------------------------------------+
                             |
                             v
+-----------------------------------------------+
|  5. Continuous Deployment to Staging (CD)     |
|     - Container registry promotion            |
|     - Rolling Kubernetes deployment rollout   |
|     - Zero-downtime health verification       |
+-----------------------------------------------+
```

---

## Multi-Platform Pipeline Implementations

1. **GitHub Actions (`.github/workflows/ci-cd.yml`)**:
   - Triggers on `push` and `pull_request` to `main` and `develop`.
   - Matrix strategy running parallel test jobs for microservices.
   - Uploads Surefire JUnit test reports and production JAR artifacts.
   - Automates continuous deployment to `staging` environment.
2. **Jenkins Pipeline (`Jenkinsfile`)**:
   - Declarative Jenkins pipeline syntax with parallel stages.
   - Integrates JUnit test reporting plugin (`junit '**/target/surefire-reports/*.xml'`).
   - Handles artifact fingerprinting and conditional deployment upon branch approval.
3. **GitLab CI/CD (`.gitlab-ci.yml`)**:
   - Multi-stage pipeline (`lint`, `test`, `package`, `deploy`).
   - Configures Maven local caching in `.m2/repository` for fast builds.

---

## Local Verification Runner

To verify the CI/CD pipeline locally on Windows before committing to Git:
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 21 - CI-CD Pipeline Implementation"
powershell -ExecutionPolicy Bypass -File .\test_ci_pipeline.ps1
```

### Verified Sample Output
```text
==========================================================
 [Task 21] Local CI/CD Pipeline Execution Runner          
==========================================================

[Stage 1/5] Checking Toolchains...
  Java:   java version "23.0.1" 2024-10-15
  Maven:  Apache Maven 3.9.16
  Node:   v20.x

[Stage 2/5] Running Node.js Syntax & Lint Verification...
  ✓ server.js and db.js syntax OK

[Stage 3/5] Running Microservice Automated Tests (Task 18)...
  ✓ All 18 automated tests passed successfully

[Stage 4/5] Packaging Microservices JAR Artifacts...
  ✓ Task 16 API Gateway packaged
  ✓ Task 17 Resilient Order Service packaged

[Stage 5/5] Continuous Deployment Simulation (CD)...
  --> Verifying JAR artifacts in target directories...
      Found Artifact: api-gateway-0.0.1-SNAPSHOT.jar (58.42 MB)
      Found Artifact: resilient-order-service-0.0.1-SNAPSHOT.jar (56.12 MB)
      Found Artifact: microservice-unit-testing-0.0.1-SNAPSHOT.jar (22.84 MB)

==========================================================
 CI/CD Pipeline Local Simulation PASSED ALL GATES!        
==========================================================
```
