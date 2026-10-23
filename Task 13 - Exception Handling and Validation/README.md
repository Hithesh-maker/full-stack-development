# Task 13: Exception Handling and Validation

## Overview
An enterprise error handling and request validation architecture built using **Jakarta Bean Validation (JSR-380)**, custom domain exceptions, and centralized exception handling via `@RestControllerAdvice` and `@ExceptionHandler`. Guarantees consistent RFC 7807 problem details and clear client feedback.

---

## Validation & Error Interception Architecture

```
[ Client Request ]
       |
       | 1. HTTP POST /products (with invalid payload)
       v
+-------------------------------+
| DispatcherServlet & Validator |  --- Fails @Valid rules (e.g. @NotBlank, @Min) ---> [MethodArgumentNotValidException]
+-------------------------------+                                                                   |
       |                                                                                            |
       v (Valid Payload)                                                                            v
+-------------------------------+                                                   +------------------------------------+
|  ProductController & Service  |  --- Missing Entity ---> [ProductNotFoundException] | GlobalExceptionHandler             |
+-------------------------------+                                   |               | (@RestControllerAdvice)            |
                                                                    +-------------> +------------------------------------+
                                                                                                    |
                                                                                                    v 2. Standardized JSON Error
                                                                                            [ HTTP 400 / 404 Response ]
```

---

## Bean Validation Constraints (`Product.java`)

```java
public class Product {
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
```

---

## Standard Error Response Structure

When validation fails, `GlobalExceptionHandler` formats field errors into an actionable map:
```json
{
  "timestamp": "2026-10-23T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "name": "Product name cannot be blank",
    "price": "Price must be greater than zero"
  },
  "path": "/products"
}
```

When an entity is not found (`ProductNotFoundException`), it returns:
```json
{
  "timestamp": "2026-10-23T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 does not exist",
  "path": "/products/999"
}
```

---

## Execution Guide

1. **Launch Service**:
   ```powershell
   cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 13 - Exception Handling and Validation"
   mvn spring-boot:run
   ```

2. **Verify Bean Validation Trigger**:
   ```powershell
   # Send invalid payload with empty name and negative price
   $invalidBody = @{
       name = ""
       description = ""
       price = -100
       quantity = -5
   } | ConvertTo-Json

   Invoke-RestMethod -Uri "http://localhost:8080/products" -Method Post -ContentType "application/json" -Body $invalidBody
   ```

3. **Verify Custom 404 Exception**:
   ```powershell
   # Request non-existent entity
   Invoke-RestMethod -Uri "http://localhost:8080/products/999" -Method Get
   ```
