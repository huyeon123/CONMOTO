package com.huyeon.apiserver.model.entity.history;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.BoardStatus;
import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
public class BoardHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "boardId")
    private Long boardId;

    private String pastTitle;

    @Enumerated(EnumType.STRING)
    private BoardStatus pastSTATUS;
}
