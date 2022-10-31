package com.huyeon.superspace.domain.board.entity.history;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class ContentBlockHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blockId;

    @Lob
    private String pastContent;

    @Builder
    public ContentBlockHistory(Long blockId, String pastContent) {
        this.blockId = blockId;
        this.pastContent = pastContent;
    }
}
