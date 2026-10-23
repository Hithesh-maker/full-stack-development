# Task 17: Resilient Inter-Service REST Communication

## Overview
A resilient inter-service communication architecture built with **Spring Cloud OpenFeign** and **Resilience4j Circuit Breaker**. Demonstrates how an upstream microservice (`resilient-order-service`, Port `8084`) securely and declaratively consumes REST APIs exposed by downstream microservices (`product-service`, Port `8081`) while gracefully intercepting network timeouts, connection refusals, and service downtime using **automated Circuit Breaker state transitions** and **Graceful Fallbacks**.

---

## Circuit Breaker State Transition & Communication Flow

```
                      +-----------------------------------+
                      |      Resilient Order Service      |
                      |            (Port: 8084)           |
                      |   - OpenFeign Declarative Client  |
                      |   - Resilience4j Circuit Breaker  |
                      +-----------------------------------+
                                        |
                 [ Check Circuit State (CLOSED / OPEN / HALF_OPEN) ]
                                        |
                 +----------------------+----------------------+
                 | (Circuit CLOSED - Healthy)                  | (Circuit OPEN / Error)
                 v                                             v
     +-----------------------+                    +-----------------------+
     | HTTP GET /products/1  |                    | Fallback Method       |
     | (Downstream Service)  |                    | (Cached Catalog Item) |
     +-----------------------+                    +-----------------------+
                 |                                             |
         [ Product Service ]                                   |
         (Port: 8081 - Active)                                 v
                 |                                  [ HTTP 200 OK Response ]
                 v                                  Status: DEGRADED_SERVICE_FALLBACK
     [ HTTP 200 OK Response ]
     Status: LIVE_INTEGRATED_SERVICE
```

---

## Technical Specifications

- **Declarative REST Client**: Spring Cloud OpenFeign (`@EnableFeignClients`, `@FeignClient`)
- **Fault Tolerance**: Resilience4j (`spring-cloud-starter-circuitbreaker-resilience4j`)
- **Service Discovery**: Eureka Client discovery of `product-service`
- **Fallback Pattern**: Declarative fallback class (`ProductClientFallback`) and method-level fallback handlers (`@CircuitBreaker(name = "productService", fallbackMethod = "getOrderDetailsFallback")`)

---

## Resilience4j Configuration (`application.yml`)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      productService:
        registerHealthIndicator: true
        slidingWindowSize: 5
        minimumNumberOfCalls: 3
        permittedNumberOfCallsInHalfOpenState: 2
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
  timelimiter:
    instances:
      productService:
        timeoutDuration: 3s
```

---

## Resilience Demonstration

### Scenario A: Normal Operation (Product Service is UP)
- **Request**: `GET http://localhost:8084/resilient-orders/101`
- **Response**:
  ```json
  {
    "orderId": 101,
    "productId": 1,
    "customerName": "Hithesh Enterprise Customer",
    "orderQuantity": 2,
    "totalPrice": 110000.0,
    "orderStatus": "CONFIRMED",
    "serviceStatus": "LIVE_INTEGRATED_SERVICE",
    "message": "Successfully retrieved live product details from Product Service.",
    "productDetails": {
      "id": 1,
      "name": "Laptop",
      "price": 55000.0,
      "quantity": 5
    }
  }
  ```

### Scenario B: Downstream Outage / Crash (Product Service is DOWN)
- **Request**: `GET http://localhost:8084/resilient-orders/101`
- **Observed Behavior**: The request does NOT fail with HTTP 500 or crash the client. Instead, Resilience4j intercepts the connection failure and returns a structured fallback response:
  ```json
  {
    "orderId": 101,
    "productId": 1,
    "customerName": "Hithesh Enterprise Customer",
    "orderQuantity": 2,
    "totalPrice": 0.0,
    "orderStatus": "CONFIRMED",
    "serviceStatus": "DEGRADED_SERVICE_FALLBACK",
    "message": "Product Service is down. Returned cached/fallback product information without crashing.",
    "productDetails": {
      "id": 1,
      "name": "Fallback Product #1",
      "price": 0.0,
      "quantity": 0
    }
  }
  ```
