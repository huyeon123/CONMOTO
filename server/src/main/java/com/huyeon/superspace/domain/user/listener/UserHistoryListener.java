package com.huyeon.superspace.domain.user.listener;

import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserHistoryRepo;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.global.model.HistoryListener;
import com.huyeon.superspace.global.support.BeanUtils;

import javax.persistence.*;

public class UserHistoryListener implements HistoryListener {
    @PrePersist
    @PreUpdate
    public void loggingHistory(Object history) {
        if (history instanceof User) {
            UserHistoryRepo userHistoryRepo = BeanUtils.getBean(UserHistoryRepo.class);

            User user = (User) history;
            UserHistory userHistory = new UserHistory(user);

            userHistory.setCreatedAt(user.getCreatedAt());

            userHistoryRepo.save(userHistory);
        }
    }
}
