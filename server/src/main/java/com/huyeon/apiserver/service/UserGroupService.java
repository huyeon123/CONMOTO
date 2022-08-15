package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.UserGroup;
import com.huyeon.apiserver.repository.UserGroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserGroupService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public void registerUserToGroup(User user, Groups group) {
        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(group)
                .build();

        userGroupRepository.save(userGroup);
    }

    public boolean expelUserFromGroup(MemberDto request, Groups group) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        userGroupRepository.deleteByUserAndGroup(user, group);
        return true;
    }
}
