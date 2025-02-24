package org.faust.channel.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChannelAspect {

    private final SSEEmitter emitter;

    public ChannelAspect(SSEEmitter emitter) {
        this.emitter = emitter;
    }

    @Pointcut("execution(* org.faust.channel.ChannelConsumer.addChannel(..))")
    public void channelPointcut() {

    }

    @AfterReturning(pointcut = "channelPointcut()")
    public void updateChannels() {
        emitter.emitEvent("channel");
    }
}
