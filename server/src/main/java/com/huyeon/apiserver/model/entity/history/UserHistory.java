package com.huyeon.apiserver.model.entity.history;

import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class UserHistory extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String pastName;

    private String pastPassword;

    private LocalDate pastBirthday;

    @Builder
    public UserHistory(String userEmail, String pastName, String pastPassword, LocalDate pastBirthday) {
        this.userEmail = userEmail;
        this.pastName = pastName;
        this.pastPassword = pastPassword;
        this.pastBirthday = pastBirthday;
    }
}
