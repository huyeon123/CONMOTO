package com.huyeon.apiserver.exception;

public class NotOnlyMemberException extends RuntimeException {
    public NotOnlyMemberException(String message) {
        super(message);
    }
}
