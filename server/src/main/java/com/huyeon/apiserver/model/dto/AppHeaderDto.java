package com.huyeon.apiserver.model.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class AppHeaderDto {
    private String groupName;
    private String userName;
}
