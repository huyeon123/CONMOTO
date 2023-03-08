package com.huyeon.superspace.domain.group.dto;

import com.huyeon.superspace.domain.group.document.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {
    private int level;
    private String gradeName;
    private String gradeDescription;
    private int postCondition;
    private int commentCondition;
    private int durationCondition;

    public LevelDto(Grade.Level level) {
        this.level = level.getLevel();
        this.gradeName = level.getGradeName();
        this.gradeDescription = level.getGradeDescription();
        this.postCondition = level.getPostCondition();
        this.commentCondition = level.getCommentCondition();
        this.durationCondition = level.getDurationCondition();
    }
}
