package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
public class UserDto {
    private String email;
    private String name;
    private LocalDate birthday;

    public UserDto(User user) {
        email = user.getEmail();
        name = user.getName();
        birthday = user.getBirthday();
    }
}
