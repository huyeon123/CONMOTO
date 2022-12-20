package com.huyeon.superspace.domain.board.entity;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.global.model.Auditable;
import com.huyeon.superspace.domain.board.listener.BoardHistoryListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class, BoardHistoryListener.class})
public class Board extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @NonNull
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private WorkGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @Enumerated(EnumType.STRING)
    private BoardStatus status;

    public String getGroupName() {
        return group.getName();
    }

    public String getGroupUrl() {
        return group.getUrlPath();
    }

    public String getCategoryName() {
        return category.getName();
    }
}

