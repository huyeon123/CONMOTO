package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.dto.base.AuditEntity;
import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.HistoryListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, HistoryListener.class})
public class User extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    @ToString.Exclude //비밀번호는 출력에서 제외
    private String password;

    private LocalDate birthday;

    private LocalDate expireDate;

    private Role role;

    public User(UserSignupRequestDto request) {
        name = request.getName();
        email = request.getEmail();
        password = request.getPassword();
        if(request.getBirthday() != null) birthday = request.getBirthday();
        role = Role.USER;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    public enum Role {USER, ADMIN}

}
