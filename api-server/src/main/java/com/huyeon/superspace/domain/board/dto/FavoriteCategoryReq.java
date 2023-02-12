package com.huyeon.superspace.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCategoryReq {
    private String groupUrl;
    private String categoryId;
    private boolean set;
}
