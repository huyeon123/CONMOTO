package com.huyeon.superspace.web.common.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class AppHeaderDto {
    private String groupName;
    private String userName;
}
