package com.huyeon.superspace.domain.group.document;

import com.huyeon.superspace.domain.group.dto.GradeDto;
import com.huyeon.superspace.domain.group.dto.LevelDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "grade")
public class Grade extends DocumentAudit {
    @Id
    private String id;

    @Field("group_url")
    private String groupUrl;

    @Field("level_list")
    private List<Level> levelList;

    public Grade(String groupUrl) {
        this.groupUrl = groupUrl;
        this.levelList = new ArrayList<>();
        addDefaultLevel();
    }

    private void addDefaultLevel() {
        Level[] levels = new Level[]{
                new Level(0, "Largo", "이제 활동을 시작하는 단계"),
                new Level(0, "Moderato", "활동에 적응한 단계"),
                new Level(0, "Allegretto", "활동에 여유가 생긴 단계"),
                new Level(0, "Presto", "활동에 중독된 단계")
        };

        levelList.addAll(Arrays.asList(levels));
    }

    public Grade(GradeDto dto) {
        this.id = dto.getId();
        this.groupUrl = dto.getGroupUrl();
        this.levelList = dto.getLevelList().stream()
                .map(Level::new)
                .collect(Collectors.toList());
    }

    public Level getLevel(int level) {
        return levelList.get(level);
    }

    public String getLevelName(int level) {
        return getLevel(level).getGradeName();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Level {
        private int level;
        @Field("grade_name")
        private String gradeName;
        @Field("grade_description")
        private String gradeDescription;
        @Field("post_condition")
        private int postCondition;
        @Field("comment_condition")
        private int commentCondition;
        @Field("duration_condition")
        private int durationCondition;

        public Level(LevelDto dto) {
            this.level = dto.getLevel();
            this.gradeName = dto.getGradeName();
            this.gradeDescription = dto.getGradeDescription();
            this.postCondition = dto.getPostCondition();
            this.commentCondition = dto.getCommentCondition();
            this.durationCondition = dto.getDurationCondition();
        }

        public Level(int level, String gradeName, String gradeDescription) {
            this.level = level;
            this.gradeName = gradeName;
            this.gradeDescription = gradeDescription;
        }
    }
}
