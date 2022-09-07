package com.huyeon.apiserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NotyReceiver {
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
}
