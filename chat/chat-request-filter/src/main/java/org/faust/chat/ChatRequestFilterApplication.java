package org.faust.chat;

import org.faust.chat.external.ChannelService;
import org.faust.chat.external.ChatService;
import org.faust.chat.external.KeycloakService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {ChannelService.class, ChatService.class, KeycloakService.class})
@EnableKafka
public class ChatRequestFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRequestFilterApplication.class, args);
    }
}
