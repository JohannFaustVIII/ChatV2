package org.faust.chat.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@FeignClient(name = "channel-repository") //TODO: to interface?
@Component
public class ChannelService {
    public boolean existsChannel(UUID channel) {
        return false;
    }
}
