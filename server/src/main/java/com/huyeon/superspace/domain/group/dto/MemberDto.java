package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.user.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
