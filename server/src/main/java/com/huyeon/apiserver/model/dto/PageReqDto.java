package com.huyeon.apiserver.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageReqDto {
    private String query;

    private LocalDateTime now;

    private int nextPage;
}
