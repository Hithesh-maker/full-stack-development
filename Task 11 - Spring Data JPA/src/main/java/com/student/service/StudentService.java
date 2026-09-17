package com.student.service;

import com.student.Student;
import com.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Find students by department
    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    // Find students older than a given age
    public List<Student> getStudentsOlderThan(int age) {
        return studentRepository.findByAgeGreaterThan(age);
    }

    // Find students of a specific age
    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    // Find students by department sorted by name
    public List<Student> getStudentsByDepartmentSorted(String department) {
        return studentRepository.findByDepartmentOrderByNameAsc(department);
    }

    // Pagination
    public Page<Student> getStudentsByDepartmentPaginated(
            String department, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("name").ascending()
        );

        return studentRepository.findByDepartment(department, pageable);
    }
}