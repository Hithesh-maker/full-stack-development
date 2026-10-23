package com.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ResilientOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResilientOrderApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  Resilient Order Service Running on Port 8084    ");
        System.out.println("  - OpenFeign Client + Resilience4j Fallback      ");
        System.out.println("  - Endpoint: /resilient-orders/{id}             ");
        System.out.println("=================================================");
    }
}
