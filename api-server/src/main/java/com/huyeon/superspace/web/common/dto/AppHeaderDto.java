package com.huyeon.superspace.web.common.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppHeaderDto {
    private String groupName;
    private String userName;
}
