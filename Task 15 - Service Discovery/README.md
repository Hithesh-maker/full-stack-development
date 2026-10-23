# Task 15: Service Discovery with Spring Cloud Netflix Eureka

## Overview
A centralized service registry and discovery server built using **Spring Cloud Netflix Eureka Server** (Port `8761`). Eliminates hardcoded IP addresses and ports in distributed architectures by allowing microservices to dynamically register themselves, broadcast periodic health heartbeats, and resolve peers by logical application name.

---

## Service Discovery Mechanism

```
                                +--------------------------------------+
                                |        Eureka Discovery Server       |
                                |             (Port: 8761)             |
                                |  +--------------------------------+  |
                                |  | Registry:                      |  |
                                |  | PRODUCT-SERVICE -> :8081       |  |
                                |  | ORDER-SERVICE   -> :8082       |  |
                                |  +--------------------------------+  |
                                +--------------------------------------+
                                     ^                            ^
                  1. Register &      |                            | 2. Fetch Registry /
                     Heartbeat       |                            |    Discover Peers
                                     v                            v
                      +---------------------+             +---------------------+
                      |   Product Service   |             |    Order Service    |
                      |    (Port: 8081)     | <---------- |    (Port: 8082)     |
                      +---------------------+   3. Call   +---------------------+
```

---

## Configuration (`application.properties`)

```properties
server.port=8761
spring.application.name=eureka-server

# Self-registration disabled for standalone server node
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Client polling configuration
eureka.server.enable-self-preservation=false
```

---

## Execution Guide

### 1. Launch Eureka Registry
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 15 - Service Discovery\eureka-server"
mvn spring-boot:run
```

### 2. Verify Eureka Dashboard
- Open your browser to `http://localhost:8761`.
- View the "Instances currently registered with Eureka" table.
- As microservices start up (e.g. `product-service`, `order-service`, `api-gateway`), they automatically appear under their respective application identifiers.
