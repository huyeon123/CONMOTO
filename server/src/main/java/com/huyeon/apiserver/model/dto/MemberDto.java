package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.User;
import lombok.*;

@Getter
public class MemberDto {
    private String name;
    private String email;
    private String groupRole;

    public MemberDto(User user, String groupRole) {
        name = user.getName();
        email = user.getEmail();
        this.groupRole = groupRole;
    }
}
