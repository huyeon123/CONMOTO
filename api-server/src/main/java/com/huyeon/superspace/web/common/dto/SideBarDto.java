package com.huyeon.superspace.web.common.dto;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.group.dto.GroupViewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SideBarDto {
    private List<GroupViewDto> groups;
    private List<CategoryDto> categories;
}
