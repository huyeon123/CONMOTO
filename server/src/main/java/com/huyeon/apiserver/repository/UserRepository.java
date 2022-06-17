package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
