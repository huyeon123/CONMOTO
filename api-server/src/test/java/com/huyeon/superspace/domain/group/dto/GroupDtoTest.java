package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.entity.WorkGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupDtoTest {
    @Test
    @DisplayName("GroupDto 생성 - AllArgsConstructor")
    void create(){
        //when, then
        assertDoesNotThrow(() -> new GroupDto("테스트그룹", "test-group", "test"));
    }

    @Test
    @DisplayName("GroupDto 생성 - WorkGroup 인자")
    void createWithWorkGroup(){
        //given
        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .build();

        //when
        GroupDto groupDto = new GroupDto(group);

        //then
        assertEquals(group.getName(), groupDto.getName());
        assertEquals(group.getUrlPath(), groupDto.getUrl());
    }
}
