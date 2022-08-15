package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupManagerService {
    private final GroupManagerRepository managerRepository;

    public void registerUserAsManager(User user, Groups group) {
        GroupManager groupManager = GroupManager.builder()
                .group(group)
                .manager(user)
                .build();

        managerRepository.save(groupManager);
    }

    public void revokeManager(User user, Groups group) {
        managerRepository.deleteByGroupAndManager(group, user);
    }
}
