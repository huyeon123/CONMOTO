package com.huyeon.superspace.global.exception;

public class NotAdminException extends RuntimeException{
    public NotAdminException(String message) {
        super(message);
    }
}
