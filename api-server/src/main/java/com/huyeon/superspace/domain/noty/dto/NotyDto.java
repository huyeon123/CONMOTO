package com.huyeon.superspace.domain.noty.dto;

import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
@NoArgsConstructor
public class NotyDto {
    private Long id;
    private NotyType type;
    private String title;
    private String body;
    private String payload;
    private String url;
    private String senderName;
    private String groupName;
    private String sendTime;

    private boolean read;

    @Builder
    public NotyDto(ReceivedNoty receiver) {
        this.id = receiver.getId();
        this.type = receiver.getType();
        this.title = receiver.getTitle();
        this.body = receiver.getBody();
        this.payload = receiver.getPayload();
        this.url = receiver.getUrl();
        this.groupName = receiver.getGroupName();
        this.senderName = receiver.getSenderName();
        this.sendTime = convertLDTtoString(receiver.getCreatedAt());
        this.read = receiver.isRead();
    }

    private String convertLDTtoString(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        //하루 이상 차이나면 날짜반환
        LocalDate nowDate = now.toLocalDate();
        LocalDate createDate = createdAt.toLocalDate();
        Period period = Period.between(createDate, nowDate);
        if (period.getDays() > 0) return createDate.toString();

        //아니라면 시간/분/초 기준으로 차이반환
        Duration diff = Duration.between(createdAt, now);
        long hours = diff.toHours();
        long minutes = diff.toMinutes();
        long seconds = diff.toSeconds();

        if (hours != 0) return hours + "시간 전";
        else if (minutes != 0) return minutes + "분 전";
        else return seconds + "초 전";
    }
}
