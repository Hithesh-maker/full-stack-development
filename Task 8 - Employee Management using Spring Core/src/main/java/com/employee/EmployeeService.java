package com.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public void addEmployee(Employee employee) {
        repository.addEmployee(employee);
    }

    public void showEmployees() {
        repository.getEmployees().forEach(System.out::println);
    }
}