package com.huyeon.superspace.domain.user.repository;

import com.huyeon.superspace.domain.user.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepo extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findAllByUserEmail(String email);
}
