package org.faust.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: for E2E to check passing, or at least Kafka container
// test requirements:
// 1. Kafka - probably test container + custom configuration to find it; https://www.baeldung.com/spring-boot-kafka-testing; but then set consumer to read content?
// 2. Eureka - disable, custom property file, try: https://stackoverflow.com/questions/44486165/how-to-disable-eureka-and-spring-cloud-config-in-a-webmvctest
// 3. Config server - disable as 2., provide kafka via 1.
// 4. Remember about GW_TOKEN_ID as gateway will be absent

// that would be E2E only of this microservice
// what if test all microservices all together? <- 1. big setup, 2. problematic to clean, 3. time to be sure that message processing happened 4. takes a lot of time; but overall can think about it

@RestController
@RequestMapping("/channels")
public class Controller {

    @Value("${spring.kafka.bootstrapServers}")
    private String testProperty;

    private final ChannelService channelService;

    public Controller(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public void addChannel(@RequestHeader("GW_TOKEN_ID") UUID tokenId, @RequestBody String name) {
        channelService.addChannel(tokenId, name);
    }
}
