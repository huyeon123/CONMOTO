package com.huyeon.frontend.controller.error;

import com.huyeon.frontend.exception.BadRequestException;
import com.huyeon.frontend.exception.ForbiddenException;
import com.huyeon.frontend.exception.NotFoundException;
import com.huyeon.frontend.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@ControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle400Error(Model model) {
        model.addAttribute("title", "400 Bad Request");
        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", "잘못된 요청입니다.");
        return "error/ClientError";
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handle401Error(Model model) {
        model.addAttribute("title", "401 Unauthorized");
        model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("message", "로그인이 필요한 페이지입니다.");
        return "error/ClientError";
    }
    
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handle403Error(Model model) {
        model.addAttribute("title", "403 Forbidden");
        model.addAttribute("statusCode", HttpStatus.FORBIDDEN.value());
        model.addAttribute("message", "접근할 수 없는 페이지입니다.");
        return "error/ClientError";
    }

    @ExceptionHandler({NullPointerException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404Error(Model model) {
        model.addAttribute("title", "404 Not Found");
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", "존재하지 않는 페이지입니다.");
        return "error/ClientError";
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle500Error(Exception e, Model model) {
        log.error(e.getMessage());
        model.addAttribute("title", "500 Internal Server Error");
        model.addAttribute("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "서버에 오류가 발생했습니다.\n관리자에게 문의바랍니다.");
        return "error/ServerError";
    }
}
