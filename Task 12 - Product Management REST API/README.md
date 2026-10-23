# Task 12: Product Management REST API

## Overview
A production-grade RESTful web service implementing standard HTTP protocol semantics (GET, POST, PUT, DELETE), JSON content negotiation, idempotent updates, and appropriate HTTP status codes (200, 201, 204, 404) for managing catalog products.

---

## REST Architectural Constraints

1. **Uniform Interface**: Resource identification via URIs (`/products`, `/products/{id}`) and representations using standard JSON.
2. **Stateless Operations**: Each request contains complete authorization and parameters; the server retains no client session context.
3. **Idempotence**:
   - `GET`, `PUT`, `DELETE` are idempotent (repeated calls produce equivalent system state).
   - `POST` creates new resources and returns `201 Created` with resource location headers.

---

## API Endpoint Matrix

| Method | URI | Description | Status Code |
|---|---|---|---|
| `GET` | `/products` | List all inventory products | `200 OK` |
| `GET` | `/products/{id}` | Retrieve specific product details | `200 OK` / `404 Not Found` |
| `POST` | `/products` | Add a new product to catalog | `201 Created` |
| `PUT` | `/products/{id}` | Fully update an existing product | `200 OK` / `404 Not Found` |
| `DELETE` | `/products/{id}` | Remove product from inventory | `204 No Content` |

---

## Sample PowerShell Test Commands

```powershell
# 1. Create a Product
$product = @{
    name = "Ultra HD Monitor"
    description = "32-inch 4K IPS Display"
    price = 32000
    quantity = 8
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/products" -Method Post -ContentType "application/json" -Body $product

# 2. Get Product by ID
Invoke-RestMethod -Uri "http://localhost:8080/products/1" -Method Get

# 3. Update Product
$update = @{
    name = "Ultra HD Monitor 4K"
    description = "Updated 32-inch IPS Display with HDR"
    price = 34500
    quantity = 12
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/products/1" -Method Put -ContentType "application/json" -Body $update

# 4. Delete Product
Invoke-RestMethod -Uri "http://localhost:8080/products/1" -Method Delete
```

---

## Execution Guide

```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 12 - Product Management REST API"
mvn spring-boot:run
```
