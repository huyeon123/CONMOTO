package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupManagerService {
    private final GroupManagerRepository managerRepository;

    public void registerUserAsManager(User user, WorkGroup group) {
        GroupManager groupManager = GroupManager.builder()
                .group(group)
                .manager(user)
                .build();

        managerRepository.save(groupManager);
    }

    public void revokeManager(User user, WorkGroup group) {
        managerRepository.deleteByGroupAndManager(group, user);
    }
}
