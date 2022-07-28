package com.huyeon.apiserver.model.entity;

import com.huyeon.apiserver.model.Authority;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.UserHistoryListener;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "userId"))
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
