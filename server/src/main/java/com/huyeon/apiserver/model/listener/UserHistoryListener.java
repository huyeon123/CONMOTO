package com.huyeon.apiserver.model.listener;

import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.history.UserHistory;
import com.huyeon.apiserver.repository.history.UserHistoryRepo;
import com.huyeon.apiserver.support.BeanUtils;

import javax.persistence.PreUpdate;

public class UserHistoryListener implements HistoryListener{
    @PreUpdate
    @Override
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
