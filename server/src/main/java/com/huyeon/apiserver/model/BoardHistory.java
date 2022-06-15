package com.huyeon.apiserver.model;

import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.HistoryListener;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {HistoryListener.class})
public class BoardHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private Long userId;

    private String title;

    private String content;
}
