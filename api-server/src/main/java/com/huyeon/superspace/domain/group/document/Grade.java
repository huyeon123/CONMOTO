package com.huyeon.superspace.domain.group.document;

import com.huyeon.superspace.domain.group.dto.GradeDto;
import com.huyeon.superspace.domain.group.dto.LevelDto;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.ArrayList;
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

    public Grade(String id, String groupUrl) {
        this.id = id;
        this.groupUrl = groupUrl;
        this.levelList = new ArrayList<>();
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

    public void setLevel(Level level) {
        levelList.set(level.getLevel(), level);
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
    }
}
