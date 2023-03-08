package com.huyeon.superspace.domain.group.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinDto {
    private String groupId;
    private String groupUrl;
    private String userEmail;
    private String nickname;
}
