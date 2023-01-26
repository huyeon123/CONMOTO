package com.huyeon.superspace.web.domain.board;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.domain.board.service.NewCommentService;
import com.huyeon.superspace.global.exception.AlreadyExistException;
import com.huyeon.superspace.domain.group.service.NewGroupService;
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
    private final NewGroupService groupService;
    private final NewBoardService boardService;
    private final NewCommentService commentService;

    @GroupPage
    @GetMapping("/{groupUrl}/board/{id}")
    public Map<String, Object> boardPage(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @PathVariable String groupUrl,
            @PathVariable String id
    ) {
        if (groupService.isNotMemberByUrl(groupUrl, userEmail)) {
            throw new AlreadyExistException("멤버가 아닙니다!");
        }

        Map<String, Object> response = new HashMap<>();

        BoardDto board = boardService.getBoard(id);

        String groupId = groupService.findGroupByUrl(groupUrl).getId();
        List<CommentDto> comments = commentService.getCommentInBoard(groupId, userEmail, board.getId(), 0);

        response.put("board", board);
        response.put("comments", comments);

        return response;
    }
}
