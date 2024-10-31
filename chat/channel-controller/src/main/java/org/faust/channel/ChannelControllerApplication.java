package org.faust.channel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import(org.faust.config.AuthenticationFilter.class)
public class ChannelControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelControllerApplication.class, args);
    }

}