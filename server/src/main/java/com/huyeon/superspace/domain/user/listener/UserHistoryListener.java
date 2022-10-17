package com.huyeon.superspace.domain.user.listener;

import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserHistoryRepo;
import com.huyeon.superspace.global.model.HistoryListener;
import com.huyeon.superspace.global.support.BeanUtils;

import javax.persistence.PreUpdate;

public class UserHistoryListener implements HistoryListener {
    @PreUpdate
    public void loggingHistory(Object history) {
        if (history instanceof User) {
            UserHistoryRepo userHistoryRepo = BeanUtils.getBean(UserHistoryRepo.class);

            User user = (User) history;
            UserHistory userHistory = UserHistory.builder()
                    .userEmail(user.getEmail())
                    .pastName(user.getName())
                    .pastPassword(user.getPassword())
                    .pastBirthday(user.getBirthday())
                    .build();

            userHistory.setCreatedAt(user.getCreatedAt());

            userHistoryRepo.save(userHistory);
        }
    }
}
