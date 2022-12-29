package com.huyeon.superspace.web.domain.user.dto;

import com.huyeon.superspace.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
public class UserDto {
    private String email;
    private String name;
    private LocalDate birthday;

    private String expireIn;

    public UserDto(User user) {
        email = user.getEmail();
        name = user.getName();
        birthday = user.getBirthday();
        expireIn = user.getExpireDate() == null ? null : user.getExpireDate().toString();
    }
}
