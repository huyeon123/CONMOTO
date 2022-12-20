package com.huyeon.superspace.domain.group.exception;

public class NotAdminException extends RuntimeException{
    public NotAdminException(String message) {
        super(message);
    }
}
