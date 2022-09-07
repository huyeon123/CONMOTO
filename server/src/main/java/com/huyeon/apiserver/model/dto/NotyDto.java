package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.NotyReceiver;
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
    private String senderName;
    private LocalDateTime sendTime;
    private String message;

    public NotyDto(NotyReceiver receiver) {
        this.id = receiver.getId();
        this.senderName = receiver.getSenderName();
        this.sendTime = receiver.getCreatedAt();
        this.message = receiver.getMessage();
    }
}
