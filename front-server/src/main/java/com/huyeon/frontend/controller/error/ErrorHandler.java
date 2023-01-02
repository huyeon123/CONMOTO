package com.huyeon.frontend.controller.error;

import com.huyeon.frontend.exception.BadRequestException;
import com.huyeon.frontend.exception.NotFoundException;
import com.huyeon.frontend.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

public class ErrorHandler {
    public static void checkError(HttpStatus status) {
        assert status != null;
        switch (status) {
            case BAD_REQUEST:
                throw new BadRequestException("STATUS CODE: " + status.value());
            case UNAUTHORIZED:
                throw new UnauthorizedException("STATUS CODE: " + status.value());
            case NOT_FOUND:
                throw new NotFoundException("STATUS CODE: " + status.value());
            case INTERNAL_SERVER_ERROR:
                throw new HttpServerErrorException(status);
        }
    }

    public static void checkError(int statusValue) {
        switch (statusValue) {
            case 400:
                throw new BadRequestException("STATUS CODE: " + statusValue);
            case 401:
                throw new UnauthorizedException("STATUS CODE: " + statusValue);
            case 404:
                throw new NotFoundException("STATUS CODE: " + statusValue);
            case 500:
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
