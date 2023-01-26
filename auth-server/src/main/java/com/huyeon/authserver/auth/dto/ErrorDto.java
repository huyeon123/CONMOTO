package com.huyeon.authserver.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
    private int ErrorCode;
    private String message;
}
