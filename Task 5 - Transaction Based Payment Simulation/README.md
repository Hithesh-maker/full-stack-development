# 💳 Task 5: Transaction-Based Payment Simulation

## 📌 Description

This project simulates a basic online payment transaction using a relational database.

The system demonstrates how digital payment applications transfer money between a **user account** and a **merchant account** while maintaining data consistency using database transactions.

The application performs the following operations:

* Deducts the payment amount from the user's account
* Adds the payment amount to the merchant's account
* Uses **COMMIT** when the transaction is successful
* Uses **ROLLBACK** when the transaction fails
* Displays the transaction status to the user

---

## 🎯 Objective

To understand and implement **database transactions** in a real-world payment scenario.

The main objective is to ensure that a payment is processed completely or not processed at all.

For example:

> If ₹500 is deducted from the user's account but cannot be added to the merchant's account, the entire transaction should be cancelled using **ROLLBACK**.

---

## 🛠️ Technologies Used

* HTML5
* CSS3
* JavaScript
* Supabase
* PostgreSQL
* SQL Transactions

---

## ⚙️ Key Features

### 👤 User Account

Stores the user's account information and available balance.

### 🏪 Merchant Account

Stores the merchant's account information and available balance.

### 💰 Payment Processing

Allows the user to enter a payment amount and process a transaction.

### ✅ COMMIT

If all payment operations are completed successfully, the transaction is committed and the updated balances are permanently saved.

### ❌ ROLLBACK

If an error occurs during the transaction, the changes are rolled back to prevent partial payment updates.

### 📊 Transaction Status

Displays whether the payment was successful or failed.

---

## 🔄 Transaction Flow

```text
User enters payment amount
          ↓
Check user balance
          ↓
Check payment validity
          ↓
Deduct amount from user
          ↓
Add amount to merchant
          ↓
      Transaction
       successful?
       ↙        ↘
     YES         NO
      ↓           ↓
   COMMIT      ROLLBACK
      ↓           ↓
Payment       No balance
successful    changes saved
```

---

## 🗄️ Database Structure

The project uses database tables to manage account and transaction information.

### Accounts Table

| Column       | Description             |
| ------------ | ----------------------- |
| id           | Unique account ID       |
| name         | Account holder name     |
| account_type | User or Merchant        |
| balance      | Current account balance |

### Transactions Table

| Column      | Description               |
| ----------- | ------------------------- |
| id          | Unique transaction ID     |
| sender_id   | User account ID           |
| receiver_id | Merchant account ID       |
| amount      | Payment amount            |
| status      | Transaction status        |
| created_at  | Transaction date and time |

---

## 🔐 Transaction Management

Database transactions are important in payment systems because multiple database operations must be treated as a single unit.

### COMMIT

When the payment succeeds:

```sql
COMMIT;
```

All changes made during the transaction are permanently saved.

### ROLLBACK

When the payment fails:

```sql
ROLLBACK;
```

All changes made during the transaction are cancelled.

This prevents problems such as money being deducted from the user without reaching the merchant.

---

## 💡 Real-Time Application

Transaction management is widely used in:

* Online banking
* UPI payments
* Credit and debit card payments
* E-commerce applications
* Digital wallets
* Payment gateways
* Financial management systems

---

## 🚀 How It Works

1. Enter the payment amount.
2. The system checks whether the user has sufficient balance.
3. The payment transaction begins.
4. The amount is deducted from the user's account.
5. The same amount is added to the merchant's account.
6. If all operations succeed, the transaction is **COMMITTED**.
7. If any operation fails, the transaction is **ROLLED BACK**.
8. The updated account balances and transaction status are displayed.

---

## 📂 Project Structure

```text
Task 5 - Transaction-Based Payment Simulation/
│
├── index.html
├── style.css
├── script.js
├── README.md
└── database/
    └── transaction.sql
```

---

## 📸 Expected Result

The application provides a simple payment interface where the user can:

* View account balances
* Enter a payment amount
* Initiate a payment
* View transaction status
* Verify updated balances

---

## 🎓 Learning Outcomes

Through this task, the following concepts are demonstrated:

* Database transactions
* COMMIT operation
* ROLLBACK operation
* Data consistency
* Atomicity
* Relational database operations
* Payment processing logic
* SQL-based transaction management

---

## 👨‍💻 Project Information

**Task:** 5 — Transaction-Based Payment Simulation
**Domain:** Full Stack Development
**Database:** Supabase PostgreSQL
**Focus:** Database Transactions and Payment Processing

---

## 📄 Conclusion

This project demonstrates how transaction management can be used to build a reliable payment system. By using **COMMIT** and **ROLLBACK**, the system ensures that payment operations remain consistent and prevents incomplete or incorrect financial transactions.
