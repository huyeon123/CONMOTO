package com.huyeon.superspace.domain.group.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateDto {
    private String groupName;
    private String description;
    private String groupUrl;
    private String nickname;
}
