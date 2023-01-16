package com.huyeon.superspace.domain.noty.entity;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class})
public class ReceivedNoty extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noty_id")
    private Noty noty;

    private String userEmail;

    private boolean isRead;

    public boolean isUnread() {
        return !isRead;
    }

    public String getSenderName() {
        return noty.getSenderName();
    }

    public LocalDateTime getCreatedAt() {
        return noty.getCreatedAt();
    }

    public String getMessage() {
        return noty.getMessage();
    }

    public String getUrl() {
        return noty.getRedirectUrl();
    }

    public NotyType getType() {
        return noty.getType();
    }

    public String getPayload() {
        return noty.getPayload();
    }
}
