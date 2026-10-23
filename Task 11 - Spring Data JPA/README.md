# Task 11: Advanced Spring Data JPA

## Overview
Demonstrates advanced data access patterns using **Spring Data JPA**, including **Derived Query Methods**, **Custom JPQL Queries (`@Query`)**, **Sorting (`Sort`)**, and server-side **Pagination (`Pageable`, `Page<T>`)** over large relational datasets.

---

## Technical Highlights

- **Derived Query Methods**: Method names transformed directly into SQL queries at runtime without manual SQL (e.g. `findByDepartment`, `findByAgeGreaterThan`).
- **Dynamic Sorting**: Utilizing `org.springframework.data.domain.Sort` to order records by name, age, or GPA without hardcoding SQL `ORDER BY`.
- **Zero-Based Pagination**: Implementing `PageRequest.of(page, size, sort)` returning total pages, total elements, and slice data for memory-efficient querying.

---

## Repository Interface Implementations

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // Derived query: Filter by department
    List<Student> findByDepartment(String department);
    
    // Derived query: Exact age match
    List<Student> findByAge(int age);
    
    // Derived query: Range condition
    List<Student> findByAgeGreaterThan(int age);
    
    // JPQL Query with Pagination and Sorting
    @Query("SELECT s FROM Student s WHERE s.department = :dept")
    Page<Student> findByDepartmentPaged(@Param("dept") String dept, Pageable pageable);
}
```

---

## API Endpoints Catalog

| Endpoint | Method | Query Pattern |
|---|---|---|
| `/students` | `GET` | Retrieve all students |
| `/students/department/{dept}` | `GET` | Filter by department (e.g. `/students/department/CSE`) |
| `/students/age/{age}` | `GET` | Exact age query (e.g. `/students/age/21`) |
| `/students/older-than/{age}` | `GET` | Age greater than parameter (e.g. `/students/older-than/23`) |
| `/students/department/{dept}/sorted` | `GET` | Department filter sorted by name ASC |
| `/students/department/{dept}/page?page=0&size=3` | `GET` | Paginated result set with page number and page size |

---

## Execution Guide

1. **Launch Application**:
   ```powershell
   cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 11 - Spring Data JPA"
   mvn spring-boot:run
   ```

2. **Test Advanced Queries**:
   ```powershell
   # 1. Query by Department
   Invoke-RestMethod -Uri "http://localhost:8080/students/department/CSE" -Method Get

   # 2. Query Paginated Slice (Page 0, 3 items)
   Invoke-RestMethod -Uri "http://localhost:8080/students/department/CSE/page?page=0&size=3" -Method Get

   # 3. Query Paginated Slice (Page 1, 3 items)
   Invoke-RestMethod -Uri "http://localhost:8080/students/department/CSE/page?page=1&size=3" -Method Get
   ```
