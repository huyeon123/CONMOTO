package com.huyeon.superspace.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageReqDto {
    private String query;

//    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
//    private LocalDateTime now;

    private int nextPage;
}
