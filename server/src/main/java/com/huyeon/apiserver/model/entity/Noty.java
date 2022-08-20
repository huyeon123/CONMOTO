package com.huyeon.apiserver.model.entity;

import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    @ElementCollection
    private List<String> receivers;

    @OneToOne(fetch = FetchType.LAZY)
    private Groups receiveGroup;

    private String message;

    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    private NotyType type;
}
