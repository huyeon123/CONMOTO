package com.huyeon.apiserver.controller.page;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.BoardDto;
import com.huyeon.apiserver.model.dto.CommentDto;
import com.huyeon.apiserver.model.dto.ContentDto;
import com.huyeon.apiserver.model.dto.TagDto;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.CommentService;
import com.huyeon.apiserver.service.ContentBlockService;
import com.huyeon.apiserver.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/workspace/{groupUrl}/board/{id}")
    public String boardPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String groupUrl,
            @PathVariable Long id, Model model) {
        BoardDto boardResponse = boardService.getBoardResponse(id);

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
