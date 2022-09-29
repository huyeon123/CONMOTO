package com.huyeon.apiserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageReqDto {
    private String query;

    private LocalDateTime now;

    private int nextPage;
}
