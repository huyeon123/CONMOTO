package com.huyeon.superspace.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserUpdateDto {
    private String name;

    private String password;

    private LocalDate birthday;
}
