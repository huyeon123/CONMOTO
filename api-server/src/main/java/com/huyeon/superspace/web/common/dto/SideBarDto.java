package com.huyeon.superspace.web.common.dto;

import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.newboard.dto.CategoryDto;
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
    private List<GroupDto> groups;
    private List<CategoryDto> categories;
}
