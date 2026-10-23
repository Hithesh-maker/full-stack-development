# Task 18: Microservices Unit Testing Suite

## Overview
A comprehensive automated testing framework for microservices using **JUnit 5**, **Mockito**, **Spring MockMvc**, and **H2 In-Memory Database**. Implements test separation across the three core layers of enterprise microservices: **Service Logic Unit Testing**, **REST Controller Web Slice Testing**, and **Data Handling Persistence Testing**.

---

## Testing Pyramid & Layer Isolation

```
           / \
          /   \         REST Controller Slice Tests (@WebMvcTest + MockMvc)
         / Web \        - Tests HTTP status codes, JSON serialization, validation errors
        /-------\       - Service layer mocked out completely using @MockBean
       / Service \      Isolated Business Logic Tests (JUnit 5 + Mockito)
      /   Logic   \     - Tests business calculations, branching, exception handling
     /-------------\    - Fast execution without loading Spring ApplicationContext
    /   Data / JPA  \   Persistence Slice Tests (@DataJpaTest + In-Memory H2)
   /   Repository    \  - Tests SQL queries, entity mappings, constraints, transactions
  /-------------------\
```

---

## Test Suites Breakdown

### 1. Service Layer Tests (`ProductServiceTest.java`)
- **Technology**: JUnit 5, `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
- **Objective**: Test pure business logic in complete isolation from databases and HTTP servers.
- **Scenarios**:
  - `testGetAllProducts_Success()`: Mocked repository returns data; verifies service mapping.
  - `testGetProductById_NotFound_ThrowsException()`: Asserts that non-existent IDs raise `ProductNotFoundException`.
  - `testAddProduct_InvalidPrice_ThrowsException()`: Asserts negative price rejection.
  - `testDeleteProduct_Success()`: Verifies verification calls to `existsById` and `deleteById`.

### 2. Controller Web Slice Tests (`ProductControllerTest.java`)
- **Technology**: `@WebMvcTest(ProductController.class)`, `MockMvc`, `@MockBean`, `ObjectMapper`
- **Objective**: Validate HTTP contracts, status codes (200, 201, 204, 400), and JSON responses without starting the full HTTP server.
- **Scenarios**:
  - `GET /products`: Validates HTTP 200 and JSON array length with `jsonPath`.
  - `POST /products`: Validates HTTP 201 and returned JSON payload.
  - `POST /products` (invalid input): Validates that `@Valid` triggers HTTP 400 Bad Request before service invocation.
  - `DELETE /products/{id}`: Validates HTTP 204 No Content.

### 3. Data Layer Persistence Tests (`ProductRepositoryTest.java`)
- **Technology**: `@DataJpaTest`, `TestEntityManager`, In-memory H2 database
- **Objective**: Validate custom Spring Data derived queries and database constraints.
- **Scenarios**:
  - `testSaveAndFindById_Success()`: Verifies entity persistence and ID generation.
  - `testFindByNameContainingIgnoreCase()`: Tests custom partial-match derived queries.
  - `testExistsByName()`: Tests existence boolean checks.
  - `testDeleteById()`: Confirms proper deletion from SQL tables.

---

## Automated Execution & Verification

Run all test suites with Maven:
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 18 - Microservices Unit Testing"
mvn clean test
```

### Expected Output Summary
```text
[INFO] Running com.testing.service.ProductServiceTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.testing.controller.ProductControllerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.testing.repository.ProductRepositoryTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
```
