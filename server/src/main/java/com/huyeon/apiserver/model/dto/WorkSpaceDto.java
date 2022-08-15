package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSpaceDto {
    private String title;
    private Board.STATUS status;
    private String categoryName;
    private List<String> comments;
    private List<String> contents;
}
