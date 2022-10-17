package com.huyeon.superspace.domain.board.entity.history;

import com.huyeon.superspace.domain.board.entity.BoardStatus;
import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class BoardHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private String pastTitle;

    @Enumerated(EnumType.STRING)
    private BoardStatus pastSTATUS;

    @Builder
    public BoardHistory(Long boardId, String pastTitle, BoardStatus pastSTATUS) {
        this.boardId = boardId;
        this.pastTitle = pastTitle;
        this.pastSTATUS = pastSTATUS;
    }
}
