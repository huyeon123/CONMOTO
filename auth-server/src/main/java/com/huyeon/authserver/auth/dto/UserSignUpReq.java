package com.huyeon.authserver.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpReq {
    private String name;

    private String email;

    private String password;

    private LocalDate birthday;
}
