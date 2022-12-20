package com.huyeon.superspace.web.domain.board;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.dto.TagDto;
import com.huyeon.superspace.domain.board.service.BoardService;
import com.huyeon.superspace.domain.board.service.CommentService;
import com.huyeon.superspace.domain.board.service.ContentBlockService;
import com.huyeon.superspace.domain.board.service.TagService;
import com.huyeon.superspace.web.annotation.GroupPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspace")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final TagService tagService;
    private final CommentService commentService;
    private final ContentBlockService blockService;

    @GroupPage
    @GetMapping("/{groupUrl}/board/{id}")
    public Map<String, Object> boardPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl,
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        BoardDto boardResponse = boardService.getBoardDto(id);

        List<TagDto> tags = tagService.getTagResponseByBoardId(boardResponse.getId());
        boardResponse.setTags(tags);

        List<CommentDto> comments = commentService.getCommentsResponseByBoardId(boardResponse.getId());
        boardResponse.setComments(comments);

        List<ContentDto> contents = blockService.getContentResponseByBoardId(boardResponse.getId());
        boardResponse.setContents(contents);

        boardResponse.setUrl("/workspace/" + groupUrl + "/board/" + id);

        response.put("board", boardResponse);

        return response;
    }
}
