package com.huyeon.superspace.domain.group.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.domain.group.dto.JoinDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Requester {
    @Field("user_email")
    private String userEmail;

    private String nickname;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm")
    private LocalDateTime timestamp;

    public Requester(String userEmail, String nickname) {
        this.userEmail = userEmail;
        this.nickname = nickname;
        this.timestamp = LocalDateTime.now();
    }

    public Requester(String userEmail, String nickname, LocalDateTime timestamp) {
        this.userEmail = userEmail;
        this.nickname = nickname;
        this.timestamp = timestamp;
    }

    public Requester(JoinDto request) {
        this.userEmail = request.getUserEmail();
        this.nickname = request.getNickname();
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requester requester = (Requester) o;

        return Objects.equals(userEmail, requester.userEmail);
    }

    @Override
    public int hashCode() {
        return userEmail != null ? userEmail.hashCode() : 0;
    }
}
