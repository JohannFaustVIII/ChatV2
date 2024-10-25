package org.faust.channel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChannelRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelRepositoryApplication.class, args);
    }

}
