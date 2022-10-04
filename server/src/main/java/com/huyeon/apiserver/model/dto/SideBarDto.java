package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.WorkGroup;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SideBarDto {
    private List<WorkGroup> groups;
    private List<CategoryDto> categories;
}
