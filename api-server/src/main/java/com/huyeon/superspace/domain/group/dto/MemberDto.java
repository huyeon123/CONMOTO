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

    private String grade;

    private int postCount;

    private int commentCount;

    private String joinDate;

    public MemberDto(Member member, String grade) {
        id = member.getId();
        email = member.getUserEmail();
        nickname = member.getNickname();
        groupUrl = member.getGroupUrl();
        role = member.getRole();
        this.grade = grade;
        postCount = member.getPostCount();
        commentCount = member.getCommentCount();
        joinDate = member.getCreatedAt().toLocalDate().toString();
    }
}

