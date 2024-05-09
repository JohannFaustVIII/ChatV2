package org.faust.chat.sse;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SSEAspect {

    private final SSEService sseService;

    public SSEAspect(SSEService sseService) {
        this.sseService = sseService;
    }


    @Pointcut("execution(* org.faust.chat.channel.ChannelService.addChannel(..))")
    public void channelPointcut() {

    }

    @Pointcut("execution(* org.faust.chat.chat.ChatService.addMessage(..))")
    public void chatPointcut() {

    }

    @AfterReturning(pointcut = "channelPointcut()")
    public void updateChannels() {
        sseService.emitEvents("channel");
    }

    @AfterReturning(pointcut = "chatPointcut()", returning = "retVal")
    public void updateChat(String retVal){
        sseService.emitEvents(retVal);
    }

}
