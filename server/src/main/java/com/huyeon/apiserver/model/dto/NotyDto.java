package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Noty;
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
    private String senderName;
    private LocalDateTime sendTime;
    private String message;

    public NotyDto(Noty noty) {
        this.senderName = noty.getSenderName();
        this.sendTime = noty.getCreatedAt();
        this.message = noty.getMessage();
    }
}
