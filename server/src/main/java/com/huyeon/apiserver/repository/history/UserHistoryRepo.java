package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.model.dto.history.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepo extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findAllByUser(User user);
}
