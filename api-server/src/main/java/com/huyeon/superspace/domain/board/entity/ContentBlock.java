package com.huyeon.superspace.domain.board.entity;

import com.huyeon.superspace.domain.board.listener.ContentBlockHistoryListener;
import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, ContentBlockHistoryListener.class})
public class ContentBlock extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Lob //varchar 보다 큰 내용을 담을 수 있음
    private String content;

    public ContentBlock(Board board) {
        this.board = board;
    }
}
