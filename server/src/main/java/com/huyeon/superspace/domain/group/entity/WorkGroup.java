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
public class WorkGroup extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String urlPath;

    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    private User owner;
}
