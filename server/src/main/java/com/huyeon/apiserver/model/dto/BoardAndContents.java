package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.ContentBlock;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BoardAndContents {
    private String title;
    private Board.STATUS status;
    private List<ContentBlock> contents;
}
