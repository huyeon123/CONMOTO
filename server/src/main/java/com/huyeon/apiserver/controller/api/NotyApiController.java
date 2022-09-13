package com.huyeon.apiserver.controller.api;

import com.huyeon.apiserver.model.EmitterAdaptor;
import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.NotyDto;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.service.NotyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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

    @PutMapping
    public ResponseEntity<?> setReadNoty(@RequestBody List<Long> idList) {
        notyService.setReadNoty(idList);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/close/{notyType}")
    public ResponseEntity<?> closeEmitter(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String notyType
    ) {
        notyService.completeEmitter(userDetails.getUsername(), notyType);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
