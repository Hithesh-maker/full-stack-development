package com.student.repository;

import com.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find students by department
    List<Student> findByDepartment(String department);

    // Find students older than a given age
    List<Student> findByAgeGreaterThan(int age);

    // Find students within a specific age
    List<Student> findByAge(int age);

    // Find students by department with sorting
    List<Student> findByDepartmentOrderByNameAsc(String department);

    // Find students by department with pagination
    Page<Student> findByDepartment(String department, Pageable pageable);
}