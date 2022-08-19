package com.huyeon.apiserver.controller;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.service.NotyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/noty")
@RequiredArgsConstructor
public class NotyApiController {
    private final NotyService notyService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader(value = "Last-Event-ID",
                    required = false,
                    defaultValue = ""
            ) String lastEventId
    ) {
        return notyService.subscribe(userDetails.getUsername(), lastEventId);
    }
}
