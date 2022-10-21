package com.huyeon.superspace.domain.user.repository;

import com.huyeon.superspace.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(value = "select u.name from User u where email = :email")
    Optional<String> findNameByEmail(@Param("email") String email);
}
