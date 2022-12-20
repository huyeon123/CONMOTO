package com.huyeon.superspace.domain.user.entity;

import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
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

    public UserHistory(User user) {
        this.userEmail = user.getEmail();
        this.pastName = user.getName();
        this.pastPassword = user.getPassword();
        this.pastBirthday = user.getBirthday();
    }
}
