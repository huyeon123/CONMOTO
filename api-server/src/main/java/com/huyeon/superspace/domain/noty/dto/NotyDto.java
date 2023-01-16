package com.huyeon.superspace.domain.noty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotyDto {
    private Long id;
    private NotyType type;
    private String url;
    private String senderName;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime sendTime;
    private String message;

    private Object payload;

    @Builder
    public NotyDto(ReceivedNoty receiver) {
        this.id = receiver.getId();
        this.type = receiver.getType();
        this.url = receiver.getUrl();
        this.senderName = receiver.getSenderName();
        this.sendTime = receiver.getCreatedAt();
        this.message = receiver.getMessage();
        this.payload = receiver.getPayload();
    }
}
