# Task 6: Automated Logging using Triggers & Views

## 📌 Description

This project demonstrates automated database logging using **SQL Triggers** and **Database Views**.

The system automatically records every `INSERT` and `UPDATE` operation performed on application data. A database view is then used to generate a daily activity report from the recorded logs.

This demonstrates how enterprise databases can automatically maintain audit records without requiring the application to manually create a log entry for every database operation.

---

## 🎯 Objective

The main objectives of this task are:

* Create a database table for application records
* Create an audit log table
* Create a trigger that automatically logs every `INSERT`
* Create a trigger that automatically logs every `UPDATE`
* Create a view for daily activity reports
* Display the audit information through a web interface

---

## 🛠️ Technologies Used

* HTML5
* CSS3
* JavaScript
* Supabase
* PostgreSQL
* SQL Triggers
* SQL Views

---

## ⚙️ Key Features

### 📝 Automated INSERT Logging

Whenever a new record is inserted into the main table, the database trigger automatically creates an entry in the audit log.

### 🔄 Automated UPDATE Logging

Whenever an existing record is updated, the trigger automatically records the operation and stores the relevant information.

### 📋 Audit Log

The audit log stores information such as:

* Record ID
* Operation type
* Old data
* New data
* Timestamp

### 📊 Daily Activity Report

A database view groups audit activities by date and operation type to provide a simple daily report.

### ⚡ Automatic Logging

The application does not need to manually insert audit records. The database trigger performs the logging automatically.

---

## 🔄 System Flow

```text
User performs INSERT / UPDATE
            ↓
       Main Database
            ↓
        SQL Trigger
            ↓
        Audit Log
            ↓
       Database View
            ↓
    Daily Activity Report
            ↓
       Web Dashboard
```

---

## 🗄️ Database Structure

### Main Table

The main table stores the application's primary records.

| Column     | Description            |
| ---------- | ---------------------- |
| id         | Unique record ID       |
| name       | Record name            |
| department | Department information |
| created_at | Record creation time   |
| updated_at | Last update time       |

### Audit Log Table

The audit table automatically stores database activities.

| Column     | Description                |
| ---------- | -------------------------- |
| id         | Unique log ID              |
| record_id  | ID of affected record      |
| operation  | INSERT or UPDATE           |
| old_data   | Previous record data       |
| new_data   | New record data            |
| changed_at | Date and time of operation |

---

## 🔥 SQL Trigger

A PostgreSQL trigger is used to automatically execute a function whenever an `INSERT` or `UPDATE` occurs.

Example:

```sql
CREATE TRIGGER audit_trigger
AFTER INSERT OR UPDATE
ON records
FOR EACH ROW
EXECUTE FUNCTION log_activity();
```

The trigger ensures that database activity is automatically captured.

---

## 📊 Daily Activity View

A database view is created to provide a summarized daily report.

Example:

```sql
CREATE VIEW daily_activity_report AS
SELECT
    DATE(changed_at) AS activity_date,
    operation,
    COUNT(*) AS total_operations
FROM audit_logs
GROUP BY
    DATE(changed_at),
    operation
ORDER BY
    activity_date DESC;
```

---

## 🌐 Web Dashboard

The frontend displays:

* Total audit activities
* INSERT operations
* UPDATE operations
* Recent audit logs
* Daily activity reports

The dashboard retrieves the information directly from the Supabase PostgreSQL database.

---

## 🏢 Real-Time Usage

Automated database logging is commonly used in:

* Banking systems
* Enterprise applications
* Healthcare systems
* E-commerce platforms
* Financial applications
* Employee management systems
* Security and compliance systems

Audit logging helps organizations track **who changed data, what operation occurred, and when it happened**.

---

## 📂 Project Structure

```text
Task 6 - Automated Logging using Triggers & Views/
│
├── index.html
├── style.css
├── script.js
├── README.md
└── database/
    └── audit_logging.sql
```

---

## 🎓 Learning Outcomes

After completing this task, the following concepts are demonstrated:

* SQL Triggers
* PostgreSQL Trigger Functions
* Automated Audit Logging
* INSERT and UPDATE monitoring
* Database Views
* Aggregation using SQL
* Daily activity reporting
* Database-level automation
* Enterprise audit systems

---

## 🚀 Expected Result

The application provides a dashboard where database activities can be monitored.

When a record is inserted or updated:

```text
INSERT / UPDATE
      ↓
Trigger executes
      ↓
Audit record created
      ↓
Daily view updated
      ↓
Dashboard displays activity
```

No manual audit-log insertion is required from the frontend.

---

## 👨‍💻 Project Information

**Task:** 6 — Automated Logging using Triggers & Views
**Domain:** Full Stack Development
**Database:** Supabase PostgreSQL
**Focus:** SQL Triggers, Audit Logging and Database Views

---

## 📄 Conclusion

This project demonstrates how database triggers can automate audit logging and how database views can simplify reporting.

By moving logging logic into the database layer, enterprise applications can maintain reliable audit trails while reducing the amount of logging logic required in application code.
