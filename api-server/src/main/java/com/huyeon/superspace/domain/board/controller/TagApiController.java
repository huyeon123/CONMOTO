package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.TagDto;
import com.huyeon.superspace.domain.board.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;

    @PutMapping
    public ResponseEntity<?> editTag(
            @RequestParam Long boardId,
            @RequestBody List<TagDto> request) {
        tagService.editTag(boardId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteTag(
            @RequestParam Long boardId,
            @RequestBody TagDto request) {
        tagService.deleteTag(request);
    }
}
