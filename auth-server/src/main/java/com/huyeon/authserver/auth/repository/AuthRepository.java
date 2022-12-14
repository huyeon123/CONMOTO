package com.huyeon.authserver.auth.repository;

import com.huyeon.authserver.auth.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    //EAGER 조회로 권한 정보를 같이 가져옴
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findWithAuthoritiesByEmail(String email);

    boolean existsByEmail(String email);
}
