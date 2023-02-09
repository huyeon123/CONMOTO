package com.huyeon.authserver.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectLoginCodeException extends RuntimeException{
    public IncorrectLoginCodeException(String message) {
        super(message);
    }
}
