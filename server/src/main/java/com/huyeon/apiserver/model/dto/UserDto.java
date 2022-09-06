package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
