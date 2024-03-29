package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentUpdateDto;
import com.huyeon.superspace.domain.board.dto.LikePostRes;
import com.huyeon.superspace.domain.board.service.LikePostService;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
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
    private final LikePostService likeService;
    private final NewGroupService groupService;

    @PostMapping("/{groupUrl}/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createBoard(
            @PathVariable String groupUrl,
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestBody BoardDto request
    ) {
        checkCreatePermission(groupUrl, userEmail);
        assert groupUrl.equals(request.getGroupUrl());
        Long boardId = boardService.createBoard(userEmail, request);
        likeService.createLikePost(boardId);
        return boardId;
    }

    @PostMapping("/{groupUrl}/temp")
    @ResponseStatus(HttpStatus.CREATED)
    public Long temporallySave(
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
    public Long updateTitle(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "TITLE");
    }

    @PutMapping("/edit/description")
    public Long updateDescription(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "DESCRIPTION");
    }

    @PutMapping("/edit/category")
    public Long updateCategory(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "CATEGORY");
    }

    @PutMapping("/edit/status")
    public Long updateStatus(
            @RequestBody BoardDto request,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        Objects.requireNonNull(request.getId());
        checkUpdatePermission(request.getId(), userEmail, "게시글 수정 권한이 없습니다.");
        return boardService.saveBoard(request, "STATUS");
    }

    @PutMapping("/edit/tags")
    public Long updateTags(
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
            @PathVariable Long id,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        checkUpdatePermission(id, userEmail, "게시글 삭제 권한이 없습니다.");
        boardService.deleteBoard(id);
    }

    @GetMapping("/like")
    public LikePostRes likeBoard(
            @RequestParam String memberId,
            @RequestParam String groupUrl,
            @RequestParam Long boardId,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        if (isNotMember(groupUrl, userEmail)) {
            throw new PermissionDeniedException("그룹 멤버만 좋아요 기능을 사용할 수 있습니다.");
        }
        return likeService.likeBoard(memberId, boardId, userEmail);
    }

    @GetMapping("/views")
    public int viewsBoard(@RequestParam Long boardId) {
        return likeService.viewsUp(boardId);
    }

    private void checkUpdatePermission(Long id, String userEmail, String errorMsg) {
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

    private boolean isNotMember(String groupUrl, String userEmail) {
        return groupService.isNotMemberByUrl(groupUrl, userEmail);
    }
}
