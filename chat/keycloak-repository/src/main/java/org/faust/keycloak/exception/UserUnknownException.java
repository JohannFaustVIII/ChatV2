package org.faust.keycloak.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Requested user not found.", value = HttpStatus.NOT_FOUND)
public final class UserUnknownException extends RuntimeException {
}
