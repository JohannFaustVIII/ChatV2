package org.faust.chat.user;

public class StatusAspect {
    // TODO: idea, update user status by monitoring hitting endpoints
    // adv: less work for frontend to send updates
    // dis: can get more problematic when user sends custom requests, can keep online forever
    // what if monitor if session is open? https://docs.spring.io/spring-security/site/docs/4.0.1.RELEASE/apidocs/org/springframework/security/core/session/SessionDestroyedEvent.html
}
