package com.huyeon.apiserver.repository.history;

import com.huyeon.apiserver.model.dto.history.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepo extends JpaRepository<UserHistory, Long> {
}
