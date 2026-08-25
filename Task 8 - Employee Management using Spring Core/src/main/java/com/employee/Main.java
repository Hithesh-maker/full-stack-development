package com.employee;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        BeanFactory factory =
                new AnnotationConfigApplicationContext(AppConfig.class);

        EmployeeService service =
                factory.getBean(EmployeeService.class);

        service.addEmployee(new Employee(101, "Hithesh", 25000));
        service.addEmployee(new Employee(102, "Arun", 30000));
        service.addEmployee(new Employee(103, "Bala", 28000));

        System.out.println("Employee Details:");
        service.showEmployees();
    }
}