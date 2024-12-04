package org.faust.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Requested channel not found.", value = HttpStatus.NOT_FOUND)
public class ChannelUnknownException extends RuntimeException {
}
