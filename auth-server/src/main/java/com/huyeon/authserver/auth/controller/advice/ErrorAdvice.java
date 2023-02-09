package com.huyeon.authserver.auth.controller.advice;

import com.huyeon.authserver.auth.dto.ErrorDto;
import com.huyeon.authserver.auth.exception.DuplicateEmailException;
import com.huyeon.authserver.auth.exception.NonMembersException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorAdvice {
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDuplicateEmailError() {
        log.warn("중복된 이메일 요청입니다.");
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), "중복된 이메일 입니다.");
    }

    @ExceptionHandler(NonMembersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleNonMembersError() {
        log.warn("가입되지 않은 사용자입니다.");
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), "가입되지 않은 사용자입니다.");
    }
}
