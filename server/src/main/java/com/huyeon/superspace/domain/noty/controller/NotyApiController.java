package com.huyeon.superspace.domain.noty.controller;

import com.huyeon.superspace.domain.noty.dto.EmitterAdaptor;
import com.huyeon.superspace.global.model.UserDetailsImpl;
import com.huyeon.superspace.domain.noty.dto.NotyDto;
import com.huyeon.superspace.domain.noty.service.NotyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/noty")
@RequiredArgsConstructor
public class NotyApiController {
    private final NotyService notyService;

    @GetMapping(value = "/subscribe/{notyType}", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String notyType,
            @RequestHeader(value = "Last-Event-ID",
                    required = false,
                    defaultValue = ""
            ) String lastEventId
    ) {
        EmitterAdaptor emitterAdaptor = EmitterAdaptor.builder()
                .userEmail(userDetails.getUsername())
                .lastEventId(lastEventId)
                .build();

        return notyService.subscribe(emitterAdaptor, notyType);
    }

    @GetMapping("/{page}")
    public ResponseEntity<?> userLatestNoty(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int page
    ) {
        List<NotyDto> latestNoty = notyService.findAllByUser(userDetails.getUsername(), page);
        return new ResponseEntity<>(latestNoty, HttpStatus.OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<?> unreadNoty(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<NotyDto> unreadEvent = notyService.sendUnreadEvent(userDetails.getUsername());
        return new ResponseEntity<>(unreadEvent, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> setReadNoty(@RequestBody List<Long> idList) {
        notyService.setReadNoty(idList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/close/{notyType}")
    public ResponseEntity<?> closeEmitter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String notyType) {
        notyService.completeEmitter(userDetails.getUsername(), notyType);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
