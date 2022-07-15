package com.huyeon.apiserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(Authority.class)
public class Authority implements GrantedAuthority {
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_SUBSCRIBER = "ROLE_SUBSCRIBER";
    public static final String ROLE_ADMIN = "ROLE_TEACHER";

    public static final Authority USER_AUTHORITY = Authority.builder().authority(ROLE_USER).build();
    public static final Authority SUBSCRIBER_AUTHORITY = Authority.builder().authority(ROLE_SUBSCRIBER).build();
    public static final Authority ADMIN_AUTHORITY = Authority.builder().authority(ROLE_ADMIN).build();

    @Id
    private Long userId;

    @Id
    private String authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        Authority targetAuth = (Authority) o;
        return Objects.equals(authority, targetAuth.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
