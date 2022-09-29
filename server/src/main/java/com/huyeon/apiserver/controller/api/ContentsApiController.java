package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.entity.ContentBlock;
import com.huyeon.apiserver.service.ContentBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsApiController {
    private final ContentBlockService blockService;

    @GetMapping("/{boardId}")
    public ResponseEntity<?> createContent(
            @PathVariable Long boardId) {
        Long blockId = blockService.createContent(boardId);
        return new ResponseEntity<>(blockId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editContents(
            @PathVariable Long id,
            @RequestBody ContentBlock request) {
        blockService.writeContents(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        blockService.removeContent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
