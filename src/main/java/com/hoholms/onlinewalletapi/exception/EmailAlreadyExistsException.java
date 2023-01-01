package com.hoholms.onlinewalletapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class EmailAlreadyExistsException extends RegisterException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
