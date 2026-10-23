# Task 14: Microservices Architecture (Product & Order Services)

## Overview
A distributed microservices architecture decomposing an e-commerce backend into autonomous, independently deployable services: **Product Service** (Port `8081`) and **Order Service** (Port `8082`). Demonstrates database-per-service pattern, RESTful boundaries, Eureka client registration, and synchronous inter-service communication via Spring `@LoadBalanced` `RestTemplate`.

---

## Architecture Topology

```
                                  +------------------------------+
                                  |    Eureka Service Registry   |
                                  |         (Port: 8761)         |
                                  +------------------------------+
                                      ^                      ^
                   Heartbeat/Register |                      | Heartbeat/Register
                                      v                      v
        +----------------------------------+            +----------------------------------+
        |          Product Service         |            |           Order Service          |
        |           (Port: 8081)           |            |           (Port: 8082)           |
        +----------------------------------+            +----------------------------------+
                        |                                                |
                        | HTTP GET /products                             | Inter-service call:
                        |<-----------------------------------------------| "http://product-service/products"
                        |                                                |
                        v                                                v
               [ Product Database ]                             [ Order Database ]
```

---

## Service Specifications

### 1. Product Service (`product-service`)
- **Port**: `8081`
- **Application Name**: `product-service`
- **Responsibilities**: Manages catalog inventory, pricing, and product entity lifecycle.
- **Key Endpoints**:
  - `GET /products`: List all products
  - `POST /products`: Add product
  - `GET /products/{id}`: Product details

### 2. Order Service (`order-service`)
- **Port**: `8082`
- **Application Name**: `order-service`
- **Responsibilities**: Manages customer order processing, order status, and inventory lookup from Product Service.
- **Key Endpoints**:
  - `GET /orders`: List all orders
  - `POST /orders`: Place a new order
  - `GET /orders/products`: Consumes `product-service` via `@LoadBalanced RestTemplate` using service discovery resolution (`http://product-service/products`).

---

## Execution Guide

### 1. Start Eureka Registry (Task 15)
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 15 - Service Discovery\eureka-server"
mvn spring-boot:run
```

### 2. Start Product Service
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 14 - Microservices\product-service"
mvn spring-boot:run
```

### 3. Start Order Service
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 14 - Microservices\order-service"
mvn spring-boot:run
```

### 4. Verify Services
- Open Eureka Console: `http://localhost:8761` (Verify both `PRODUCT-SERVICE` and `ORDER-SERVICE` status `UP`).
- Direct Product Endpoint: `http://localhost:8081/products`
- Inter-Service Order Call: `http://localhost:8082/orders/products`
