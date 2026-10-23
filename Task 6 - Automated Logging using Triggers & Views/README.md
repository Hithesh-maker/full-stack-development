# Task 6: Automated Logging using Database Triggers & Views

## Overview
An automated audit logging and reporting architecture utilizing MySQL **Triggers** for non-intrusive CDC (Change Data Capture) and **Views** for aggregated analytical reporting. Captures INSERT, UPDATE, and DELETE actions on core business entities (`customers`) and logs old/new state changes into an immutable `audit_logs` table.

---

## Architecture Diagram

```
[ Application / API / Admin ]
             |
             | (INSERT / UPDATE / DELETE on 'customers')
             v
+-------------------------------+      Automated Trigger Fire      +-------------------------------+
|      Table: customers         |  ----------------------------->  |       Table: audit_logs       |
| (customer_id, name, city...)  |     (after_customer_update)      |  (log_id, old_data, new_data) |
+-------------------------------+                                  +-------------------------------+
             |
             | Queried via Analytical View
             v
+-------------------------------+
|   View: daily_activity_summary|
|   (Aggregations & Reports)    |
+-------------------------------+
```

---

## Preserved SQL Implementations (`task6.sql`)

### 1. Audit Log Table
```sql
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    record_id INT,
    old_data TEXT,
    new_data TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. UPDATE Trigger Capturing Delta
```sql
DELIMITER $$
CREATE TRIGGER after_customer_update
AFTER UPDATE ON customers
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (
        table_name,
        operation_type,
        record_id,
        old_data,
        new_data
    )
    VALUES (
        'customers',
        'UPDATE',
        NEW.customer_id,
        CONCAT('name: ', OLD.name, ', city: ', OLD.city),
        CONCAT('name: ', NEW.name, ', city: ', NEW.city)
    );
END$$
DELIMITER ;
```

### 3. Analytical Reporting View
```sql
CREATE OR REPLACE VIEW daily_activity_summary AS
SELECT 
    DATE(changed_at) AS log_date,
    table_name,
    operation_type,
    COUNT(*) AS total_operations
FROM audit_logs
GROUP BY DATE(changed_at), table_name, operation_type
ORDER BY log_date DESC;
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/audit-logs` | Retrieves full immutable audit history |
| `GET` | `/api/reports/daily-activity` | Queries `daily_activity_summary` view |
| `PUT` | `/api/customers/:id` | Triggers the `after_customer_update` trigger automatically |

---

## Project Structure

```text
Task 6 - Automated Logging using Triggers & Views/
├── task6.sql        # Executable SQL script with tables, triggers, and views
├── index.html       # Audit log visualization & report viewer UI
├── style.css        # Tabular UI styling with diff highlights
├── script.js        # Audit log data consumer
└── README.md        # Comprehensive task documentation
```

---

## Execution Guide

1. **Apply Database Triggers & Views**:
   ```bash
   mysql -u root -p studenthub < "Task 6 - Automated Logging using Triggers & Views/task6.sql"
   ```
2. **Start Backend**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
3. **Trigger Logs**:
   - Update a customer record via the UI or API.
   - Query `SELECT * FROM audit_logs;` to inspect automated trigger entries.