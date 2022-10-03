package com.huyeon.apiserver.model.entity;

import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
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
}
