package com.huyeon.superspace.domain.user.dto;

import com.huyeon.superspace.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
public class UserDto {
    private final String email;
    private final String name;
    private final LocalDate birthday;

    private final String expireIn;

    public UserDto(User user) {
        email = user.getEmail();
        name = user.getName();
        birthday = user.getBirthday();
        expireIn = user.getExpireDate() == null ? null : user.getExpireDate().toString();
    }
}
