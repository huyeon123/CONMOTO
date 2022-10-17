package com.huyeon.superspace.domain.group.entity;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.global.model.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class})
public class GroupManager extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User manager;

    @OneToOne(fetch = FetchType.LAZY)
    private WorkGroup group;


    public GroupManager(User manager, WorkGroup group) {
        this.group = group;
        this.manager = manager;
    }
}
