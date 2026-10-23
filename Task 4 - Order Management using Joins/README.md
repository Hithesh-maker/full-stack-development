# Task 4: Order Management using SQL Joins

## Overview
A comprehensive order management system demonstrating relational database operations across multiple normalized tables (`customers`, `orders`, and `products`). Features complex SQL multi-table joins (INNER JOIN, LEFT JOIN) and aggregate summary metrics.

---

## Relational Schema Diagram

```
+--------------------+            +--------------------+            +--------------------+
|     customers      | 1        * |       orders       | *        1 |      products      |
+--------------------+------------+--------------------+------------+--------------------+
| customer_id (PK)   |            | order_id (PK)      |            | product_id (PK)    |
| name               |            | customer_id (FK)   |            | name               |
| email              |            | product_id (FK)    |            | price              |
| city               |            | quantity           |            | stock_quantity     |
+--------------------+            | total_amount       |            +--------------------+
                                  | order_date         |
                                  | status             |
                                  +--------------------+
```

---

## Core SQL Queries Preserved

### 1. Multi-Table Inner Join (Order Details with Customer & Product)
```sql
SELECT 
    o.order_id,
    c.name AS customer_name,
    c.email,
    c.city,
    p.name AS product_name,
    o.quantity,
    p.price,
    o.total_amount,
    o.order_date,
    o.status
FROM orders o
INNER JOIN customers c ON o.customer_id = c.customer_id
INNER JOIN products p ON o.product_id = p.product_id
ORDER BY o.order_date DESC;
```

### 2. Left Outer Join (All Customers with Order History)
```sql
SELECT 
    c.customer_id,
    c.name AS customer_name,
    c.city,
    COUNT(o.order_id) AS total_orders,
    COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.city
ORDER BY total_spent DESC;
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/orders` | Retrieves all orders joined with customer and product data |
| `GET` | `/api/orders/summary` | Aggregate order statistics (total revenue, order counts) |
| `GET` | `/api/customers` | Retrieves all customers |
| `GET` | `/api/products` | Retrieves all available products |

---

## Project Structure

```text
Task 4 - Order Management using Joins/
├── index.html       # Order dashboard UI with tabular views
├── style.css        # Clean grid layout for master-detail records
├── script.js        # Dynamic table generation and join data presentation
├── Csv Files/       # Sample seed datasets for customers, orders, products
└── README.md        # Comprehensive task documentation
```

---

## Execution Guide

1. **Start Backend**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
2. **Open Front-End**:
   - Open `Task 4 - Order Management using Joins/index.html` in your browser.
3. **Verify Queries**:
   - Inspect table joining customer names with purchased product lines.
   - Verify aggregate metrics showing total revenue and orders per customer.