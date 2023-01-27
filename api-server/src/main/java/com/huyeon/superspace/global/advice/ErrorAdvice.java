package com.huyeon.superspace.global.advice;

import com.huyeon.superspace.global.exception.BadRequestException;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import com.huyeon.superspace.global.dto.ErrorDto;
import com.huyeon.superspace.global.exception.AlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handle403Error() {
        return new ErrorDto(403, "해당 페이지에 접근할 수 없습니다.");
    }

    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handlePermissionDenied(
            @RequestHeader("X-Authorization-Id") String userEmail,
            Exception e
    ) {
        log.info("[{}: 권한 없음] {}", userEmail, e.getMessage());
        return new ErrorDto(403, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequest(
            @RequestHeader("X-Authorization-Id") String userEmail,
            Exception e
    ) {
        log.info("[{}: 잘못된 요청] {}", userEmail, e.getMessage());
        return new ErrorDto(400, e.getMessage());
    }

}
