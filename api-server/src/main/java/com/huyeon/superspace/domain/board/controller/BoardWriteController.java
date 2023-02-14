package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentUpdateDto;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardWriteController {
    private final NewBoardService boardService;
    private final NewGroupService groupService;

    @PostMapping("/{groupUrl}/new")
    @ResponseStatus(HttpStatus.CREATED)
    public String createBoard(
            @PathVariable String groupUrl,
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody BoardDto request
    ) {
        checkCreatePermission(groupUrl, userEmail);
        assert groupUrl.equals(request.getGroupUrl());
        return boardService.createBoard(userEmail, request);
    }

    @PostMapping("/{groupUrl}/temp")
    @ResponseStatus(HttpStatus.CREATED)
    public String temporallySave(
            @PathVariable String groupUrl,
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody BoardDto request
    ) {
        checkCreatePermission(groupUrl, userEmail);
        assert groupUrl.equals(request.getGroupUrl());
        return boardService.saveTemporally(userEmail, request);
    }

    private void checkCreatePermission(String groupUrl, String userEmail) {
        if (groupService.isNotMemberByUrl(groupUrl, userEmail)) {
            throw new PermissionDeniedException("게시글 생성 권한이 없습니다.");
        }
    }

    @PutMapping("/edit/title")
    public String updateTitle(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "TITLE");
    }

    @PutMapping("/edit/description")
    public String updateDescription(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "DESCRIPTION");
    }

    @PutMapping("/edit/category")
    public String updateCategory(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "CATEGORY");
    }

    @PutMapping("/edit/status")
    public String updateStatus(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "STATUS");
    }

    @PutMapping("/edit/tags")
    public String updateTags(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "TAGS");
    }

    @PutMapping("/edit/content")
    public String updateContent(
            @RequestBody ContentUpdateDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getBoardId());
        checkUpdatePermission(request.getBoardId(), userEmail, "컨텐츠 수정 권한이 없습니다.");
        return boardService.saveContent(request);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(
            @PathVariable String id,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        checkUpdatePermission(id, userEmail, "게시글 삭제 권한이 없습니다.");
        boardService.deleteBoard(id);
    }

    private void checkUpdatePermission(String id, String userEmail, String errorMsg) {
        BoardDto board = boardService.getBoard(id);
        if (isNotAuthor(board.getAuthor(), userEmail)
                && isNotManager(board.getGroupUrl(), userEmail)) {
            throw new PermissionDeniedException(errorMsg);
        }
    }

    private boolean isNotAuthor(String author, String userEmail) {
        return !author.equals(userEmail);
    }

    private boolean isNotManager(String groupUrl, String userEmail) {
        return groupService.isNotManager(groupUrl, userEmail);
    }
}
