package com.huyeon.apiserver.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserSignUpReq {
    private String name;

    private String email;

    private String password;

    private LocalDate birthday;
}
