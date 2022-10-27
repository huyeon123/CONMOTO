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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final TagService tagService;
    private final CommentService commentService;
    private final ContentBlockService blockService;

    @GroupPage
    @GetMapping("/workspace/{groupUrl}/board/{id}")
    public String boardPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupUrl,
            @PathVariable Long id, Model model) {
        BoardDto boardResponse = boardService.getBoardDto(id);

        List<TagDto> tags = tagService.getTagResponseByBoardId(boardResponse.getId());
        boardResponse.setTags(tags);

        List<CommentDto> comments = commentService.getCommentsResponseByBoardId(boardResponse.getId());
        boardResponse.setComments(comments);

        List<ContentDto> contents = blockService.getContentResponseByBoardId(boardResponse.getId());
        boardResponse.setContents(contents);

        boardResponse.setUrl("/workspace/" + groupUrl + "/board/" + id);

        model.addAttribute("board", boardResponse);

        return "pages/board";
    }
}
