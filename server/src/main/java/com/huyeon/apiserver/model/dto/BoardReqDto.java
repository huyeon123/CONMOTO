package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.BoardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardReqDto {
    private Long id;

    private String title;

    private String description;

    private Long categoryId;

    private BoardStatus status;

    private String target;
}
