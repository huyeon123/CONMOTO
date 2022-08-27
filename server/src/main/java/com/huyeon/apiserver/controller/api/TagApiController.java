package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.dto.TagDto;
import com.huyeon.apiserver.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        try {
            tagService.editTag(boardId, request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
