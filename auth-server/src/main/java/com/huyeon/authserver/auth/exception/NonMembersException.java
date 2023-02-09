package com.huyeon.authserver.auth.exception;

public class NonMembersException extends RuntimeException{
    public NonMembersException() {
        super("가입되지 않은 사용자입니다.");
    }
}
