package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.dto.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;

import com.huyeon.apiserver.model.listener.HistoryListener;
import com.huyeon.apiserver.model.listener.PersistListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, HistoryListener.class})
public class ContentBlock extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private String content;


}
