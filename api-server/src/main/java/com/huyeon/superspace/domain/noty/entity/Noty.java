package com.huyeon.superspace.domain.noty.entity;

import com.huyeon.superspace.global.model.AuditEntity;
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
public class Noty extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;

    private String message;

    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    private NotyType type;
}
