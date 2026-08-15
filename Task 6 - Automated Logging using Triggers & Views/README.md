# Task 06 — Automated Logging using Triggers & Views

## 📌 Project Overview

This project demonstrates automated database activity logging using
PostgreSQL **Triggers** and **Views**.

The system automatically records INSERT and UPDATE operations performed
on database records and generates a daily activity report.

The project is implemented using HTML, CSS, JavaScript and Supabase
PostgreSQL.

---

## 🎯 Objective

The objective of this task is to:

- Create database triggers for automated logging
- Record INSERT operations
- Record UPDATE operations
- Store old and new record data
- Generate a daily activity report using a database view
- Display database activity through a web dashboard

---

## 🛠️ Technologies Used

- HTML5
- CSS3
- JavaScript
- Supabase
- PostgreSQL
- Database Triggers
- Database Views
- Row Level Security (RLS)

---

## 🗄️ Database Structure

### Records Table

The `records` table stores the main application records.

Example fields:

- `id`
- `name`
- `department`
- `created_at`
- `updated_at`

---

### Audit Logs Table

The `audit_logs` table stores automatically generated activity logs.

Example fields:

- `id`
- `record_id`
- `operation`
- `old_data`
- `new_data`
- `changed_at`

---

### Database Trigger

A PostgreSQL trigger automatically executes whenever a record is
inserted or updated.

Flow:

    INSERT / UPDATE
          ↓
       Trigger
          ↓
      audit_logs

This removes the need for manual logging from the application.

---

## 📊 Daily Activity Report

The project uses a PostgreSQL view called:

`daily_activity_report`

The view summarizes database activity by date and operation.

Example:

| Activity Date | Operation | Total Operations |
|---------------|-----------|------------------|
| 2026-08-15 | INSERT | 5 |
| 2026-08-15 | UPDATE | 2 |

The view is queried using:

```sql
SELECT *
FROM public.daily_activity_report;