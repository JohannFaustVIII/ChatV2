package org.faust.chat.sse;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.faust.chat.user.UserRepository;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SSEAspect {

    private final SSEService sseService;

    public SSEAspect(SSEService sseService, UserRepository userRepository) {
        this.sseService = sseService;
        userRepository.addListener(this::updateUsers);
    }


    @Pointcut("execution(* org.faust.chat.channel.ChannelService.addChannel(..))")
    public void channelPointcut() {

    }

    @Pointcut("execution(* org.faust.chat.chat.ChatService.addMessage(..))")
    public void chatPointcut() {

    }

    @Pointcut("execution(* org.faust.chat.user.UserService.set*(..))")
    public void userPointcut() {

    }

    @AfterReturning(pointcut = "channelPointcut()")
    public void updateChannels() {
        sseService.emitEvents("channel");
    }

    @AfterReturning(pointcut = "chatPointcut()", returning = "retVal")
    public void updateChat(String retVal){
        sseService.emitEvents(retVal);
    }

    @AfterReturning(pointcut = "userPointcut()")
    public void updateUsers() {
        sseService.emitEvents("users");
    }
}
