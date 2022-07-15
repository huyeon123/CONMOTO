package com.huyeon.apiserver.model.entity;

import com.huyeon.apiserver.model.Authority;
import com.huyeon.apiserver.model.dto.UserSignUpReq;
import com.huyeon.apiserver.model.entity.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.HistoryListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, HistoryListener.class})
public class User extends AuditEntity implements Auditable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    private String password;

    private LocalDate birthday;

    private boolean enabled;

    private LocalDate expireDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "userId"))
    private Set<Authority> authorities;

    public User(UserSignUpReq request) {
        name = request.getName();
        email = request.getEmail();
        password = request.getPassword();
        if(request.getBirthday() != null) birthday = request.getBirthday();
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
