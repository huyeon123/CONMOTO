package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.dto.base.AuditEntity;
import com.huyeon.apiserver.model.dto.history.BoardHistory;
import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.HistoryListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class, HistoryListener.class})
public class Board extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long userId;

    @NonNull
    private String title;

    @NonNull
    private String content; //STATUS로 변경, List<ContentBlock> 추가

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "boardId", insertable = false, updatable = false)
    private List<BoardHistory> boardHistoryList = new ArrayList<>();
}

