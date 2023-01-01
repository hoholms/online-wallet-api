package com.hoholms.onlinewalletapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordsDontMatchException extends RegisterException {
    public PasswordsDontMatchException(String message) {
        super(message);
    }
}
