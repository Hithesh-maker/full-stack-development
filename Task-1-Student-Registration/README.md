# Task 1: Student Registration & Data Storage

## Overview
A full-stack student registration system that accepts student details through an interactive front-end web form, validates user inputs on both client and server layers, and persists the records into a relational MySQL database via a RESTful API powered by Node.js and Express.

---

## Architecture Diagram

```
+--------------------------------+         HTTP POST /api/students         +--------------------------------+
|       Front-End Client         |  ------------------------------------>  |        Express Server          |
|  (HTML5, CSS3, JavaScript)     |                                         |          (server.js)           |
+--------------------------------+  <------------------------------------  +--------------------------------+
                                              JSON Response                                |
                                                                                           | SQL INSERT
                                                                                           v
                                                                           +--------------------------------+
                                                                           |         MySQL Database         |
                                                                           |       (Table: students)        |
                                                                           +--------------------------------+
```

---

## Technical Specifications

- **Front-End**: Semantic HTML5, Responsive CSS3 Grid/Flexbox, Vanilla JavaScript (ES6+ `fetch` API)
- **Back-End**: Node.js, Express.js (`server.js`)
- **Database**: MySQL 8.x (`mysql2/promise` connection pool in `db.js`)
- **Database Schema**:
  ```sql
  CREATE TABLE IF NOT EXISTS students (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(100) NOT NULL,
      email VARCHAR(100) NOT NULL UNIQUE,
      phone VARCHAR(20) NOT NULL,
      dob DATE NOT NULL,
      department VARCHAR(100) NOT NULL,
      address TEXT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  ```

---

## API Specification

### Register Student
- **Endpoint**: `POST /api/students`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "name": "Jane Doe",
    "email": "jane.doe@example.com",
    "phone": "+1-555-0199",
    "dob": "2003-05-15",
    "department": "Computer Science",
    "address": "123 Academic Way, Tech City"
  }
  ```
- **Response (`201 Created`)**:
  ```json
  {
    "success": true,
    "message": "Student registered successfully",
    "studentId": 1
  }
  ```
- **Validation Error (`400 Bad Request`)**:
  ```json
  {
    "success": false,
    "message": "Name, email, phone, dob, and department are required."
  }
  ```

---

## Project Structure

```text
Task-1-Student-Registration/
├── index.html       # Student registration web interface
├── style.css        # Responsive styling and form UI
├── script.js        # Form validation and asynchronous API submission
├── Note.txt         # Execution quick-start commands
└── README.md        # Task documentation
```

---

## Step-by-Step Execution Guide

1. **Start the Central Express Backend**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
   *Expected output*: `✓ MySQL database connected` on port `3000` (or `PORT` specified in `.env`).

2. **Open the Front-End Form**:
   - Open `Task-1-Student-Registration/index.html` in any web browser, or serve via Live Server.

3. **Verify Data Submission**:
   - Fill in student details and submit.
   - Check the network tab for `POST /api/students` returning HTTP 201.
   - Verify records in MySQL:
     ```sql
     SELECT * FROM students ORDER BY id DESC LIMIT 5;
     ```
