package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.Grade;
import com.huyeon.superspace.domain.group.dto.LevelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class GradeDto {
    private String id;
    private String groupUrl;
    private List<LevelDto> levelList;

    public GradeDto(String id, String groupUrl) {
        this.id = id;
        this.groupUrl = groupUrl;
        this.levelList = new ArrayList<>();
    }

    public GradeDto(Grade grade) {
        this.id = grade.getId();
        this.groupUrl = grade.getGroupUrl();
        this.levelList = grade.getLevelList().stream()
                .map(LevelDto::new)
                .collect(Collectors.toList());
    }

    public LevelDto getLevel(int level) {
        return levelList.get(level);
    }

    public void setLevel(LevelDto level) {
        levelList.set(level.getLevel(), level);
    }
}
