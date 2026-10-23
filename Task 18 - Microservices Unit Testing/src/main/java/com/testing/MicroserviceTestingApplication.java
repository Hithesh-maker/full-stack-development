package com.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroserviceTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceTestingApplication.class, args);
        System.out.println("Microservice Testing Application running on port 8085");
    }
}
