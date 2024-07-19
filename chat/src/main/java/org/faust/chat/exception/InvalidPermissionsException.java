package org.faust.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Invalid permissions to perform requested action.", value = HttpStatus.FORBIDDEN)
public final class InvalidPermissionsException extends RuntimeException {
}
