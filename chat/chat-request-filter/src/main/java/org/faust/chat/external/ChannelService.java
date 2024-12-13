package org.faust.chat.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "channel-repository")
public interface ChannelService {
    @GetMapping("/channels/exists/{id}")
    boolean existsChannel(@PathVariable("id") UUID channelId);
}
