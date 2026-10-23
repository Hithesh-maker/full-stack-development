# Task 2: Data Retrieval & Sorting Dashboard

## Overview
A dynamic student management dashboard providing robust data retrieval, multi-attribute client/server sorting (by Name ascending/descending, Date of Birth), department-based filtering, and department-level aggregation metrics.

---

## Architecture Diagram

```
+------------------------------------+      HTTP GET /api/students       +------------------------------------+
|         Dashboard Front-End        |  ------------------------------->  |          Express Backend           |
| (Dynamic Table, Filter & Sorters)  |                                    |            (server.js)             |
+------------------------------------+  <-------------------------------  +------------------------------------+
                                                JSON Records List                            |
                                                                                             | SQL SELECT ... ORDER BY
                                                                                             v
                                                                          +------------------------------------+
                                                                          |           MySQL Database           |
                                                                          |          (Table: students)         |
                                                                          +------------------------------------+
```

---

## Technical Specifications

- **Front-End**: Semantic HTML5, CSS3 Grid/Flexbox with dark-mode aesthetic, Vanilla JS DOM manipulation.
- **Back-End**: Node.js & Express (`server.js`).
- **Database Query**:
  ```sql
  -- Retrieval with multi-column sorting
  SELECT id, name, email, phone, dob, department, address, created_at
  FROM students
  ORDER BY name ASC;
  ```

---

## API Specification

### Fetch All Students
- **Endpoint**: `GET /api/students`
- **Response (`200 OK`)**:
  ```json
  [
    {
      "id": 1,
      "name": "Alice Johnson",
      "email": "alice@university.edu",
      "phone": "+1-555-0101",
      "dob": "2002-04-12",
      "department": "Computer Science",
      "address": "404 Circuit Lane",
      "created_at": "2026-09-01T10:00:00.000Z"
    }
  ]
  ```

---

## Features

1. **Multi-Attribute Sorting**:
   - Name: Ascending (A to Z) & Descending (Z to A)
   - Date of Birth: Oldest to Newest & Newest to Oldest
2. **Dynamic Department Filtering**:
   - Filter dropdown by Computer Science, Electronics, Mechanical, Civil, Information Technology.
3. **Department Analytics Summary**:
   - Real-time count of enrolled students per department displayed in summary pill cards.

---

## Project Structure

```text
Task 2 - Data Retrieval & Sorting Dashboard/
├── index.html       # Dashboard UI with controls and student record table
├── style.css        # Responsive dashboard styling
├── script.js        # Data retrieval, sorting algorithms, and filtering logic
└── README.md        # Comprehensive task documentation
```

---

## Execution Guide

1. **Start the Express API Server**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
2. **Open Dashboard**:
   - Open `Task 2 - Data Retrieval & Sorting Dashboard/index.html` in your web browser.
3. **Verify Functionality**:
   - Click "Sort A-Z" / "Sort Z-A" to test sorting.
   - Select a department from the dropdown to test filtering.
