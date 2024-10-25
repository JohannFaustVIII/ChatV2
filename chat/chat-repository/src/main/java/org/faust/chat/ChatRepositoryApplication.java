package org.faust.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChatRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRepositoryApplication.class, args);
    }

}