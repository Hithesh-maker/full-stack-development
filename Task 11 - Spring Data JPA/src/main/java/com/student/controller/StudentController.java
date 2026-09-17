package com.student.controller;

import com.student.Student;
import com.student.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Find students by department
    @GetMapping("/department/{department}")
    public List<Student> getStudentsByDepartment(
            @PathVariable String department) {

        return studentService.getStudentsByDepartment(department);
    }

    // Find students older than a given age
    @GetMapping("/older-than/{age}")
    public List<Student> getStudentsOlderThan(
            @PathVariable int age) {

        return studentService.getStudentsOlderThan(age);
    }

    // Find students by exact age
    @GetMapping("/age/{age}")
    public List<Student> getStudentsByAge(
            @PathVariable int age) {

        return studentService.getStudentsByAge(age);
    }

    // Find students by department sorted by name
    @GetMapping("/department/{department}/sorted")
    public List<Student> getStudentsByDepartmentSorted(
            @PathVariable String department) {

        return studentService.getStudentsByDepartmentSorted(department);
    }

    // Pagination
    @GetMapping("/department/{department}/page")
    public Page<Student> getStudentsPaginated(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        return studentService.getStudentsByDepartmentPaginated(
                department, page, size);
    }
}