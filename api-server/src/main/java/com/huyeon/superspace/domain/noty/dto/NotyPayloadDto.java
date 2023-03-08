package com.huyeon.superspace.domain.noty.dto;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotyPayloadDto {
    private NotyType type;
    private String groupUrl;
    private GroupDto group;
    private String boardId;
    private BoardDto board;
    private CommentDto comment;
    private String requesterEmail;
    private String receiverEmail;
    private List<String> receiverEmailList;
    private String data;
}
