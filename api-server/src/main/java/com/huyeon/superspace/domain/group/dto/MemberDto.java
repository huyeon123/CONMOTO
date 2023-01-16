package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.Member;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String id;

    private String email;

    private String nickname;

    private String groupUrl;

    private String role;

    public MemberDto(Member member) {
        id = member.getGroup().getId();
        email = member.getUserEmail();
        nickname = member.getNickname();
        groupUrl = member.getGroup().getUrl();
        role = member.getRole();
    }
}

