package com.huyeon.apiserver.model.entity;

import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Noty extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;

    @OneToOne(fetch = FetchType.LAZY)
    private Groups group;

    private String message;

    private String redirectUrl;
}
