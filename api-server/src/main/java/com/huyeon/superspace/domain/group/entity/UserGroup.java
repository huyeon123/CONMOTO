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
@Entity
@Builder
@EntityListeners(value = {AuditingEntityListener.class})
public class UserGroup extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private WorkGroup group;

    public UserGroup(User user, WorkGroup group) {
        this.user = user;
        this.group = group;
    }
}
