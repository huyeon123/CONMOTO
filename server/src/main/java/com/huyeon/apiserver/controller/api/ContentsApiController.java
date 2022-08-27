package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.ResMessage;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.service.BoardService;
import com.huyeon.apiserver.service.ContentBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsApiController {
    private final BoardService boardService;
    private final ContentBlockService blockService;

    @GetMapping("/{boardId}")
    public ResponseEntity<?> createContent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId) {
        if (checkNotMine(userDetails.getUsername(), boardId)) {
            return new ResponseEntity<>("접근할 수 없는 게시글입니다.", HttpStatus.OK);
        }
        Long blockId = blockService.createContent(boardId);
        ResMessage resMessage = ResMessage.builder().success(true).data(blockId).build();
        return new ResponseEntity<>(resMessage, HttpStatus.OK);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<?> editContents(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId, @RequestParam Long contentId,
            @RequestBody ContentBlock request) {
        //boardId 게시글이 해당 유저의 것이 맞는지 확인
        if (checkNotMine(userDetails.getUsername(), boardId)) {
            return new ResponseEntity<>("접근할 수 없는 게시글입니다.", HttpStatus.OK);
        }
        //맞다면 request로 내용 덮어쓰기
        boolean success = blockService.writeContents(contentId, request);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteContent(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long boardId, @RequestParam Long id) {
        if (checkNotMine(userDetails.getUsername(), boardId)) {
            return new ResponseEntity<>("접근할 수 없는 게시글입니다.", HttpStatus.OK);
        }

        return new ResponseEntity<>(blockService.removeContent(id), HttpStatus.OK);
    }

    protected boolean checkNotMine(String email, Long boardId) {
        return !boardService.getBoard(boardId)
                .getUserEmail()
                .equals(email);
    }
}
