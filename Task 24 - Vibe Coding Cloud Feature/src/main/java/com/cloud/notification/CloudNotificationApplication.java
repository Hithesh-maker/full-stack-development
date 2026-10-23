package com.cloud.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudNotificationApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Cloud Notification Dispatcher Running on :8086  ");
        System.out.println(" - Secured with X-API-KEY Authentication         ");
        System.out.println(" - Cloud Health Probes: /actuator/health         ");
        System.out.println("=================================================");
    }
}
