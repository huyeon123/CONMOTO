package com.huyeon.superspace.domain.board.dto;

import com.huyeon.superspace.domain.board.entity.Tag;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private Long id;
    private String tag;

    public TagDto(Tag tag) {
        this.id = tag.getId();
        this.tag = tag.getTag();
    }
}
