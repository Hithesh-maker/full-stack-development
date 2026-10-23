# Task 3: Login System with Validation

## Overview
A secure user authentication interface featuring client-side form validation, dynamic real-time error messaging, and secure back-end credential verification against a MySQL relational database.

---

## Architecture Diagram

```
+------------------------------------+        POST /api/login (email, password)       +------------------------------------+
|          Login Client              |  --------------------------------------------> |          Express Backend           |
| (Input Validation, Dynamic Alerts) |                                                |            (server.js)             |
+------------------------------------+  <-------------------------------------------- +------------------------------------+
                                                      JWT / Auth Status                                  |
                                                                                                         | Parameterized Query
                                                                                                         v
                                                                                      +------------------------------------+
                                                                                      |           MySQL Database           |
                                                                                      |          (Table: students)         |
                                                                                      +------------------------------------+
```

---

## Technical Specifications

- **Front-End**: HTML5, CSS3 with modern glassmorphism layout, JavaScript (Regex validation, DOM event listeners).
- **Back-End**: Node.js & Express (`server.js`).
- **Database Query**:
  ```sql
  SELECT id, name, email, department 
  FROM students 
  WHERE email = ? AND password = ? 
  LIMIT 1;
  ```

---

## Validation Rules

1. **Email Format**: Valid RFC 5322 regex validation pattern (`^[^\s@]+@[^\s@]+\.[^\s@]+$`).
2. **Password Length**: Minimum 6 characters required.
3. **Empty Field Check**: Highlight fields and show distinct error indicators below inputs.

---

## API Specification

### User Login
- **Endpoint**: `POST /api/login`
- **Request Body**:
  ```json
  {
    "email": "user@university.edu",
    "password": "Password123!"
  }
  ```
- **Success Response (`200 OK`)**:
  ```json
  {
    "success": true,
    "message": "Login successful",
    "user": {
      "id": 1,
      "name": "Alice Johnson",
      "email": "user@university.edu"
    }
  }
  ```
- **Unauthorized Response (`401 Unauthorized`)**:
  ```json
  {
    "success": false,
    "message": "Invalid email or password"
  }
  ```

---

## Project Structure

```text
Task 3 - Login System with Validation/
├── index.html       # Authentication UI
├── style.css        # Responsive card layout with error animations
├── script.js        # Input validation logic and API call
├── login cred.txt   # Sample credentials for testing
└── README.md        # Task documentation
```

---

## Execution Guide

1. **Start the API Server**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development"
   node server.js
   ```
2. **Open the Login Page**:
   - Open `Task 3 - Login System with Validation/index.html`.
3. **Test Validation**:
   - Submit empty fields to observe client-side error states.
   - Enter invalid format email to see regex validation.
   - Enter credentials from `login cred.txt` to verify successful login.
