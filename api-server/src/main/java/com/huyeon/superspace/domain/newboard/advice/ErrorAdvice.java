package com.huyeon.superspace.domain.newboard.advice;

import com.huyeon.superspace.domain.newboard.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorAdvice {
    @ExceptionHandler({HttpClientErrorException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handle400Error(@RequestBody Object request) {
        log.error("[RequestBody] : {}", request.toString());
        return new ErrorDto(400, "잘못된 요청입니다. 요구 Spec을 확인해주세요.");
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ErrorDto handle204Error() {
        return new ErrorDto(204, "해당 컨텐츠가 없습니다.");
    }
}
