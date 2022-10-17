package com.huyeon.superspace.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Entity
public class Authority implements GrantedAuthority {
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_SUBSCRIBER = "ROLE_SUBSCRIBER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

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
