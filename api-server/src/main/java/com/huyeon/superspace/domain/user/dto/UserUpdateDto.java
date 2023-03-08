package com.huyeon.superspace.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserUpdateDto {
    private String name;

    private LocalDate birthday;

    private String current;
    private String password;
    private String again;
}
