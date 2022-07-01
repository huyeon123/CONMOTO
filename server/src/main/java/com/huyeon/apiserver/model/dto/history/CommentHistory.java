package com.huyeon.apiserver.model.dto.history;

import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class CommentHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "commentId")
    private Long commentId;

    private String pastComment;
}
