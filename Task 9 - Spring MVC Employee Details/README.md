# Task 9: Spring MVC Employee Details

## Overview
A web-based enterprise application built with **Spring MVC** following the classic Model-View-Controller design pattern. Demonstrates request dispatching via `DispatcherServlet`, controller mapping (`@Controller`, `@GetMapping`), model attribute binding (`ModelMap`), and dynamic view rendering with JSP/JSTL deployed on Apache Tomcat.

---

## Spring MVC Request Lifecycle

```
[ Browser Client ]
        |
        | 1. HTTP GET /employee/employees
        v
+-----------------------+     2. Handler Mapping      +----------------------+
|   DispatcherServlet   | ------------------------->  |  EmployeeController  |
+-----------------------+                             +----------------------+
        |                                                        |
        | 4. Returns Model & View Name ("employee-list")         | 3. Returns Model Data
        v                                                        v
+-----------------------+
|  InternalResourceView |
|       Resolver        |
+-----------------------+
        |
        | 5. Resolves to /WEB-INF/views/employee-list.jsp
        v
+-----------------------+     6. Rendered HTML
|       JSP View        | -------------------------> [ Browser Client ]
+-----------------------+
```

---

## Technical Specifications

- **Framework**: Spring Framework (Spring Web MVC)
- **Deployment**: Servlet Container (Apache Tomcat 11.x / WAR deployment)
- **View Technology**: JavaServer Pages (JSP) & JSTL
- **Controller Annotations**: `@Controller`, `@RequestMapping("/employees")`, `@GetMapping`

---

## Key Files

- `src/main/java/com/employee/controller/EmployeeController.java`: Handles requests and supplies employee lists to the Model.
- `src/main/webapp/WEB-INF/views/employee-list.jsp`: View template using JSTL `<c:forEach>` to render employee tables.
- `pom.xml`: Packaging set to `war` with servlet API and Spring MVC dependencies.

---

## Execution Guide

1. **Build WAR Artifact**:
   ```bash
   cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 9 - Spring MVC Employee Details"
   mvn clean package
   ```
2. **Deploy to Apache Tomcat**:
   - Copy `target/employee.war` into the Tomcat `webapps/` folder.
   - Or start Tomcat using:
     ```powershell
     cd "C:\apache-tomcat-11.0.25\bin"
     $env:JAVA_HOME="C:\Program Files\Java\jdk-23"
     $env:Path="$env:JAVA_HOME\bin;$env:Path"
     .\startup.bat
     ```
3. **Access Application**:
   - URL: `http://localhost:8080/employee/employees`
