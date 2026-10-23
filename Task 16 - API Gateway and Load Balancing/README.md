# Task 16: Spring Cloud API Gateway & Client-Side Load Balancing

## Overview
An enterprise reverse proxy and API Gateway built with **Spring Cloud Gateway** and **Spring Cloud LoadBalancer**. Serves as the unified single entry point (`http://localhost:8080`) for all client applications, providing dynamic path-based routing, client-side round-robin load balancing across multiple service instances registered with **Eureka Service Discovery**, request/response header transformation, and real-time latency auditing.

---

## Gateway Architecture & Load Balancing Topology

```
                       [ Web / Mobile / External Clients ]
                                        |
                                        | HTTP Requests (Port: 8080)
                                        v
                       +---------------------------------+
                       |    Spring Cloud API Gateway     |
                       |          (Port: 8080)           |
                       |  - Global Logging Filter        |
                       |  - Route Predicates             |
                       |  - Spring Cloud LoadBalancer    |
                       +---------------------------------+
                                        |
            +---------------------------+---------------------------+
            | (Resolve service IDs via Eureka Server :8761)         |
            v                                                       v
+-----------------------+                               +-----------------------+
|  lb://product-service |                               |  lb://order-service   |
+-----------------------+                               +-----------------------+
   | (Round Robin) |                                                |
   v               v                                                v
+--------------+ +--------------+                       +-----------------------+
| Instance #1  | | Instance #2  |                       | Instance #1           |
| (Port: 8081) | | (Port: 8083) |                       | (Port: 8082)          |
+--------------+ +--------------+                       +-----------------------+
```

---

## Technical Specifications

- **Framework**: Spring Boot 3.5.5 / Spring Cloud 2025.0.0 (Greenwich/202x train)
- **Gateway Engine**: Spring Cloud Gateway (Reactive WebFlux non-blocking event loop)
- **Service Discovery**: Spring Cloud Netflix Eureka Client (`@EnableDiscoveryClient`)
- **Load Balancer**: Spring Cloud LoadBalancer (Client-side Round-Robin algorithm)
- **Actuator & Metrics**: `/actuator/gateway/routes`, `/actuator/health`

---

## Route Configuration (`application.yml`)

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Load Balanced Route for Product Service
        - id: product-service-route
          uri: lb://product-service
          predicates:
            - Path=/products/**
          filters:
            - AddResponseHeader=X-Gateway-Route, product-service-loadbalanced
            - AddResponseHeader=X-Served-By, Spring-Cloud-Gateway

        # Load Balanced Route for Order Service
        - id: order-service-route
          uri: lb://order-service
          predicates:
            - Path=/orders/**
          filters:
            - AddResponseHeader=X-Gateway-Route, order-service-loadbalanced
            - AddResponseHeader=X-Served-By, Spring-Cloud-Gateway
```

---

## Global Filter Implementation (`LoggingGlobalFilter.java`)
Implements `GlobalFilter` and `Ordered` with precedence `-1`:
- Intercepts every inbound request to capture timestamp, HTTP method, URI, and remote IP address.
- Hooks into reactive response stream (`Mono.fromRunnable`) to measure exact downstream latency in milliseconds and log final HTTP status codes.

---

## Execution & Load Balancing Demonstration

### 1. Start Eureka Registry (Task 15)
```powershell
cd "..\Task 15 - Service Discovery\eureka-server"
mvn spring-boot:run
```

### 2. Start Product Service Instances
Open two terminal windows to spawn two separate instances:
```powershell
# Instance 1 on Port 8081
cd "..\Task 14 - Microservices\product-service"
mvn spring-boot:run

# Instance 2 on Port 8083
cd "..\Task 14 - Microservices\product-service"
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"
```

### 3. Launch API Gateway
```powershell
cd "..\Task 16 - API Gateway and Load Balancing"
mvn spring-boot:run
```

### 4. Verify Gateway Routing & Load Balancing
Send requests to the API Gateway port `8080`:
```powershell
# Inbound calls routed to lb://product-service
Invoke-RestMethod -Uri "http://localhost:8080/products" -Method Get
Invoke-RestMethod -Uri "http://localhost:8080/orders" -Method Get

# Check active routes in Gateway Actuator
Invoke-RestMethod -Uri "http://localhost:8080/actuator/gateway/routes" -Method Get
```
Observe the API Gateway console log: requests are automatically alternated and distributed between instance on port `8081` and instance on port `8083`.
