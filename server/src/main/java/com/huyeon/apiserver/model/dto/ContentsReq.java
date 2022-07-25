package com.huyeon.apiserver.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ContentsReq {
    private Integer order;
    private List<String> contents;
}
