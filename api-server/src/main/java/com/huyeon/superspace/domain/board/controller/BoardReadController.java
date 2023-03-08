package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.document.TempPost;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.dto.LikePostRes;
import com.huyeon.superspace.domain.board.service.*;
import com.huyeon.superspace.global.exception.BadRequestException;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardReadController {
    private final NewCategoryService categoryService;
    private final NewBoardService boardService;
    private final PopularBoardService popularBoardService;

    private final LikePostService likePostService;

    private final NewCommentService commentService;

    @GetMapping("/{id}")
    public BoardDto getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    @GetMapping("/latest")
    public List<BoardDto> getLatestBoardInGroup(
            @RequestParam String categoryId,
            @RequestParam Long lastIndex,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        return boardService.getNextCategory(categoryId, lastIndex, page);
    }

    @GetMapping("/latest/all")
    public List<BoardDto> getNextGroup(
            @RequestParam String url,
            @RequestParam Long lastIndex,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        return boardService.getNextGroup(url, lastIndex, page);
    }

    @GetMapping("/latest/notice")
    public List<BoardDto> getNextNotice(
            @RequestParam String url,
            @RequestParam Long lastIndex,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        String noticeId = categoryService.getCategoryByName(url, "⭐공지사항").getId();
        return boardService.getNextNotice(noticeId, lastIndex, page);
    }

    @GetMapping("/latest/popular")
    public Map<String, Object> getPopularBoards(@RequestParam String url) {
        return popularBoardService.getPopularBoards(url);
    }

    @GetMapping("/latest/member")
    public List<BoardDto> getLatestBoardInUser(
            @RequestParam String memberId,
            @RequestParam String type,
            @RequestParam Long lastIndex,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        switch (type) {
            case "write":
                return boardService.getNextMember(memberId, lastIndex, page);
            case "commented":
                return commentService.getNextCommentedPosts(memberId, lastIndex, page);
            case "like":
                return likePostService.getNextLike(memberId, lastIndex, page);
            default:
                throw new BadRequestException("잘못된 type 요청입니다.");
        }
    }

    @GetMapping("/content/{id}")
    public ContentDto getContent(@PathVariable String id) {
        return boardService.getContentById(id);
    }

    @GetMapping("/temp/{id}")
    public TempPost getTempPost(
            @RequestHeader("X-Authorization-Id") String email,
            @PathVariable Long id
    ) {
        TempPost tempPost = boardService.findTempPostById(id);
        if (tempPost.getAuthor().equals(email)) {
            return tempPost;
        } else {
            log.warn("[접근 제한] 소유자: {} / 요청자: {}", tempPost.getAuthor(), email);
            throw new PermissionDeniedException("올바르지 않은 요청입니다.");
        }
    }

    @GetMapping("/likes")
    public LikePostRes likesBoard(
            @RequestHeader("X-Authorization-Id") String email,
            @RequestParam Long boardId
    ) {
        return likePostService.getBoardLikes(email, boardId);
    }
}
