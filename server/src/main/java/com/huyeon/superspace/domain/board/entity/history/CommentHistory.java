package com.huyeon.superspace.domain.board.entity.history;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class CommentHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long commentId;

    private String pastComment;

    @Builder
    public CommentHistory(Long commentId, String pastComment) {
        this.commentId = commentId;
        this.pastComment = pastComment;
    }
}
