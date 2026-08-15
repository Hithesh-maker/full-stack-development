# Task 4 - Order Management Using Joins

## 📌 Description

This project implements an Order Management System using a relational database.

The application manages:

- Customers
- Products
- Orders
- Customer order history
- Highest-value order
- Most-active customer

The project demonstrates relational database concepts such as:

- JOIN
- Subqueries
- ORDER BY
- GROUP BY
- Foreign Keys
- Relational Database Queries

---

## 🎯 Objective

To create a web-based order management dashboard that retrieves and displays customer order information using relational database queries.

The system connects Customers, Orders, and Products using foreign-key relationships.

---

## ✨ Features

### Customer Order History

Displays:

- Order ID
- Customer Name
- Product Name
- Product Category
- Quantity
- Total Amount
- Order Date

### Highest Value Order

Finds the order with the highest total amount using a subquery.

### Most Active Customer

Identifies the customer who has placed the highest number of orders.

### Order Sorting

Orders can be sorted by:

- Newest Order
- Oldest Order
- Highest Amount
- Lowest Amount
- Customer Name

### Dashboard Statistics

The dashboard displays:

- Total Orders
- Total Customers
- Highest Value Order
- Most Active Customer

---

## 🗄️ Database Structure

The project uses three main tables.

### 1. Customers

Stores customer information.

| Column | Description |
|---|---|
| id | Primary Key |
| name | Customer Name |
| email | Customer Email |

### 2. Products

Stores product information.

| Column | Description |
|---|---|
| id | Primary Key |
| name | Product Name |
| category | Product Category |
| price | Product Price |

### 3. Orders

Stores order information.

| Column | Description |
|---|---|
| id | Primary Key |
| customer_id | Foreign Key → Customers |
| product_id | Foreign Key → Products |
| quantity | Ordered Quantity |
| order_date | Date of Order |
| total_amount | Total Order Amount |

---

## 🔗 Relationships

The database follows this relationship:

```text
Customers
    │
    │ customer_id
    ▼
  Orders
    │
    │ product_id
    ▼
 Products