package com.huyeon.superspace.domain.auth.dto;

import lombok.*;

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
