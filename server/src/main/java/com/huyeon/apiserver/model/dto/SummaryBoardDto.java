package com.huyeon.apiserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryBoardDto {
    private String boardName;
    private String boardDescription;
    private List<String> tagged;
}
