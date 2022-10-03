package com.huyeon.apiserver.model.entity.history;

import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@NoArgsConstructor
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
