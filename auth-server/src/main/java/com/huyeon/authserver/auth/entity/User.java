package com.huyeon.authserver.auth.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huyeon.authserver.auth.dto.UserSignUpReq;
import com.huyeon.authserver.oauth.model.AuthProvider;
import lombok.*;
import org.kohsuke.randname.RandomNameGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
public class User {
    @Id
    private String email;

    private String name;

    private String password;

    private LocalDate birthday;

    private boolean enabled;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "user_email"))
    private Set<Authority> authorities = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User(UserSignUpReq request) {
        name = generateRandomName();
        email = request.getEmail();
        password = request.getLoginCode();
        enabled = true;
        authorities.add(new Authority(Authority.ROLE_USER));
    }

    private String generateRandomName() {
        return new RandomNameGenerator().next();
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
