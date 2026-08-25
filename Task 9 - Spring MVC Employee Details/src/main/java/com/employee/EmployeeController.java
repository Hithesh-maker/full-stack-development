package com.employee;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    @GetMapping("/employees")
    public String showEmployees(Model model) {

        Employee employee = new Employee(101, "Hithesh", 25000);

        model.addAttribute("employee", employee);

        return "employees";
    }
}