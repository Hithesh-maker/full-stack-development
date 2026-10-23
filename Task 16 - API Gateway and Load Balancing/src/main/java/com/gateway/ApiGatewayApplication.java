package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  Spring Cloud API Gateway Started on Port 8080   ");
        System.out.println("  Load Balanced Routes:                           ");
        System.out.println("  - /products/** -> lb://product-service          ");
        System.out.println("  - /orders/**   -> lb://order-service            ");
        System.out.println("=================================================");
    }
}
