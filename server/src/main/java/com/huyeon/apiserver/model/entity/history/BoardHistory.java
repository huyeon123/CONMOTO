package com.huyeon.apiserver.model.entity.history;

import com.huyeon.apiserver.model.entity.BoardStatus;
import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
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
