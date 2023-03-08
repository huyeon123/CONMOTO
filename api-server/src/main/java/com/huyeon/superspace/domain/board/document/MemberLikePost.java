package com.huyeon.superspace.domain.board.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "member_like_post")
public class MemberLikePost {
    private static final long MAX_VALUE
            = BigInteger.valueOf(2).pow(53).subtract(BigInteger.ONE).longValue();
    @Id
    private String id; //Member Id와 공유

    private List<LikePostInfo> likes;

    public MemberLikePost(String id) {
        this.id = id;
        likes = new ArrayList<>();
    }

    public List<LikePostInfo> getNextLikes(Long lastIndex, int page) {
        final int SIZE = 50;

        //lastIndex (PostId)의 Index를 구함
        int elementIdx = indexOf(lastIndex);
        if (elementIdx == -1 && lastIndex == MAX_VALUE) elementIdx = likes.size();
        elementIdx -= SIZE * page;
        //해당 Index - 1부터 Size만큼 요소를 뺴옴
        List<LikePostInfo> next = new LinkedList<>();
        for (int i = elementIdx - 1; i >= elementIdx - SIZE && i >= 0; i--) {
            next.add(likes.get(i));
        }

        return next;
    }

    public void addLikePost(Long postId) {
        likes.add(new LikePostInfo(postId));
    }

    public void removeLikePost(Long postId) {
        likes.remove(new LikePostInfo(postId));
    }

    private int indexOf(Long lastIndex) {
        return likes.indexOf(new LikePostInfo(lastIndex));
    }

    @Getter
    @Setter
    public static class LikePostInfo {
        private Long postId;
        private LocalDateTime timestamp;

        public LikePostInfo(Long postId) {
            this.postId = postId;
            this.timestamp = LocalDateTime.now();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LikePostInfo that = (LikePostInfo) o;

            return Objects.equals(postId, that.postId);
        }

        @Override
        public int hashCode() {
            return postId != null ? postId.hashCode() : 0;
        }
    }
}
