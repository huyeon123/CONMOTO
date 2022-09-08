package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.WorkGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SideBarDto {
    private List<WorkGroup> groups;
    private List<CategoryDto> categories;
}
