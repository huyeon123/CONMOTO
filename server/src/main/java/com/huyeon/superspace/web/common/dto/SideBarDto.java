package com.huyeon.superspace.web.common.dto;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SideBarDto {
    private List<WorkGroup> groups;
    private List<CategoryDto> categories;
}
