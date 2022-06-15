package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
