package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.WorkGroup;
import lombok.*;

@Getter
@AllArgsConstructor
public class GroupDto {
    private String name;
    private String url;
    private String description;

    @Builder
    public GroupDto(WorkGroup group) {
        name = group.getName();
        url = group.getUrlPath();
        description = group.getDescription();
    }
}
