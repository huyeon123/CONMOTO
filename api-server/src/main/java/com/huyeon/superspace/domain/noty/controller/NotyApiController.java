package com.huyeon.superspace.domain.noty.controller;

import com.huyeon.superspace.domain.noty.dto.EmitterAdaptor;
import com.huyeon.superspace.domain.noty.dto.NotyDto;
import com.huyeon.superspace.domain.noty.service.NotyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/noty")
@RequiredArgsConstructor
public class NotyApiController {
    private final NotyService notyService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestHeader(value = "Last-Event-ID",
                    required = false,
                    defaultValue = ""
            ) String lastEventId
    ) {
        EmitterAdaptor emitterAdaptor = EmitterAdaptor.builder()
                .userEmail(userEmail)
                .lastEventId(lastEventId)
                .build();

        return notyService.subscribe(emitterAdaptor);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> userLatestNoty(
            @RequestHeader("X-Authorization-Id") String userEmail,
            @RequestParam int page
    ) {
        List<NotyDto> latestNoty = notyService.findAllByUser(userEmail, page);
        return new ResponseEntity<>(latestNoty, HttpStatus.OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<?> unreadNoty(@RequestHeader("X-Authorization-Id") String userEmail) {
        List<NotyDto> unreadEvent = notyService.findUnreadEvent(userEmail);
        return new ResponseEntity<>(unreadEvent, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> setReadNoty(
            @RequestBody List<Long> idList,
            @RequestHeader("X-Authorization-Id") String userEmail
    ) {
        notyService.setReadNoty(idList, userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
