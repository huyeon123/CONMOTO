package com.huyeon.superspace.domain.board.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "like_post")
public class LikePost extends DocumentAudit {
    @Id
    private Long id; //BoardId와 공유

    private int like;

    private Set<UserInfo> users;

    public LikePost(Long id) {
        this.id = id;
        this.like = 0;
        this.users = new HashSet<>();
    }

    public boolean contains(String userEmail) {
        return users.contains(new UserInfo(userEmail));
    }

    public void like(String userEmail) {
        users.add(new UserInfo(userEmail));
        like++;
    }

    public void dislike(String userEmail) {
        users.remove(new UserInfo(userEmail));
        like--;
    }

    @Getter
    private class UserInfo {
        private String userEmail;

        @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
        private LocalDateTime timestamp;

        public UserInfo(String userEmail) {
            this.userEmail = userEmail;
            this.timestamp = LocalDateTime.now();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserInfo userInfo = (UserInfo) o;

            return Objects.equals(userEmail, userInfo.userEmail);
        }

        @Override
        public int hashCode() {
            return userEmail != null ? userEmail.hashCode() : 0;
        }
    }
}
