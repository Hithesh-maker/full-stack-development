# Task 5: Transaction-Based Payment Simulation

## Overview
A financial transaction simulation demonstrating **ACID (Atomicity, Consistency, Isolation, Durability)** compliance in relational database management systems. Implements atomic fund transfers between accounts with balance validation, debiting, crediting, transaction logging, and automatic rollback on failure.

---

## Transaction Lifecycle Diagram

```
       [START TRANSACTION]
                |
                v
     [Verify Sender Balance]  ---- Insufficient Funds ---> [ROLLBACK & RETURN ERROR]
                |
                v (Sufficient)
     [Debit Sender Account]
                |
                v
     [Credit Receiver Account]
                |
                v
    [Insert Transaction Log]  ---- Any Error Occurs -----> [ROLLBACK TO INITIAL STATE]
                |
                v (All Succeeded)
            [COMMIT]
```

---

## SQL Transaction Logic Preserved

```sql
START TRANSACTION;

-- 1. Check & Debit Sender
UPDATE accounts 
SET balance = balance - 500.00 
WHERE account_id = 101 AND balance >= 500.00;

-- 2. Credit Receiver
UPDATE accounts 
SET balance = balance + 500.00 
WHERE account_id = 102;

-- 3. Record Audit Transaction
INSERT INTO transactions (from_account_id, to_account_id, amount, status, timestamp)
VALUES (101, 102, 500.00, 'SUCCESS', NOW());

COMMIT;
```

---

## API Specification

### Execute Transfer
- **Endpoint**: `POST /api/payments/transfer`
- **Request Body**:
  ```json
  {
    "fromAccountId": 101,
    "toAccountId": 102,
    "amount": 250.00
  }
  ```
- **Success (`200 OK`)**:
  ```json
  {
    "success": true,
    "message": "Transfer completed successfully",
    "transactionId": "TXN-89421",
    "debited": 250.00
  }
  ```
- **Failure - Insufficient Funds (`400 Bad Request`)**:
  ```json
  {
    "success": false,
    "message": "Transaction failed: Insufficient funds in source account. Rollback executed."
  }
  ```

---

## Project Structure

```text
Task 5 - Transaction Based Payment Simulation/
├── index.html       # Transfer simulation interface with balance cards
├── style.css        # Banking UI styling with status badges
├── script.js        # Transaction submission and live balance update handler
└── README.md        # Comprehensive task documentation
```

---

## Execution Guide

1. **Launch Express API Server**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
2. **Open Front-End**:
   - Open `Task 5 - Transaction Based Payment Simulation/index.html`.
3. **Simulate Scenarios**:
   - Transfer an amount lower than balance: Verify `COMMIT` and balance deduction.
   - Transfer an amount exceeding balance: Verify `ROLLBACK` and error message without data corruption.
