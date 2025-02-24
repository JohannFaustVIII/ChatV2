package org.faust.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserSSEApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserSSEApplication.class, args);
    }
}
