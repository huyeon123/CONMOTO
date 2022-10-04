package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.NotyType;
import com.huyeon.apiserver.model.entity.ReceivedNoty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotyDto {
    private Long id;
    private NotyType type;
    private String url;
    private String senderName;
    private LocalDateTime sendTime;
    private String message;

    @Builder
    public NotyDto(ReceivedNoty receiver) {
        this.id = receiver.getId();
        this.type = receiver.getType();
        this.url = receiver.getUrl();
        this.senderName = receiver.getSenderName();
        this.sendTime = receiver.getCreatedAt();
        this.message = receiver.getMessage();
    }
}
