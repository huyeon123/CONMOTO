package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.NotyType;
import com.huyeon.apiserver.model.entity.ReceivedNoty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotyDto {
    private Long id;
    private NotyType type;
    private String url = "#";
    private String senderName;
    private LocalDateTime sendTime;
    private String message;

    public NotyDto(ReceivedNoty receiver) {
        this.id = receiver.getId();
        this.type = receiver.getType();
        this.url = receiver.getUrl();
        this.senderName = receiver.getSenderName();
        this.sendTime = receiver.getCreatedAt();
        this.message = receiver.getMessage();
    }
}
