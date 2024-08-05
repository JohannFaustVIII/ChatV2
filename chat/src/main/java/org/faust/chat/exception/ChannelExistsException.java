package org.faust.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Channel with given name exists.", value = HttpStatus.BAD_REQUEST)
public class ChannelExistsException extends RuntimeException {
}
