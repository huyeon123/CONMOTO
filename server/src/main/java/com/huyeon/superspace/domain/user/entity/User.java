package com.huyeon.superspace.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.listener.UserHistoryListener;
import com.huyeon.superspace.global.model.AuditEntity;
import com.huyeon.superspace.global.model.Auditable;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, UserHistoryListener.class})
public class User extends AuditEntity implements Auditable {
    @Id
    @NonNull
    private String email;

    @NonNull
    private String name;

    @NonNull
    private String password;

    private LocalDate birthday;

    private boolean enabled;

    private LocalDate expireDate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "user_email"))
    private Set<Authority> authorities = new HashSet<>();

    public User(UserSignUpReq request) {
        name = request.getName();
        email = request.getEmail();
        password = request.getPassword();
        enabled = true;
        authorities.add(new Authority(Authority.ROLE_USER));
        if(request.getBirthday() != null) birthday = request.getBirthday();
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
