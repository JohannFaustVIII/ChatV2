package org.faust.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class ChatRequestFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRequestFilterApplication.class, args);
    }
}
