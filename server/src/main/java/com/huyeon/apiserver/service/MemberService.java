package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserGroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final GroupRepository groupRepository;
    private final GroupManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Transactional
    public void saveMemberRole(String groupUrl, List<MemberDto> request) {
        WorkGroup group = findGroupByUrl(groupUrl);

        for (MemberDto member : request) {
            User user = findUserByEmail(member.getEmail());
            String currentRole = checkRole(group, user);
            String changedRole = member.getGroupRole();

            if (isChanged(currentRole, changedRole)) {
                //일반 멤버 -> 관리자
                if (changedRole.equals("그룹 관리자")) {
                    registerUserAsManager(user, group);
                    continue;
                }
                //관리자 -> 일반 멤버
                if (changedRole.equals("일반 멤버")) {
                    revokeManager(user, group);
                }
            }
        }
    }

    private WorkGroup findGroupByUrl(String groupUrl) {
        return groupRepository.findByUrlPath(groupUrl).orElseThrow();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public String checkRole(WorkGroup group, User user) {
        if (isOwner(group, user)) {
            return "그룹 소유자";
        }
        if (isManager(group, user)) {
            return "그룹 관리자";
        }

        return "일반 멤버";
    }

    private boolean isOwner(WorkGroup group, User user) {
        return group.getOwner().equals(user);
    }

    private boolean isManager(WorkGroup group, User user) {
        return managerRepository.existsByGroupAndManager(group, user);
    }

    private boolean isChanged(String currentRole, String changedRole) {
        return !currentRole.equals(changedRole);
    }

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

    public void expelUser(String groupUrl, MemberDto request) {
        WorkGroup group = findGroupByUrl(groupUrl);
        User user = findUserByEmail(request.getEmail());
        userGroupRepository.deleteByUserAndGroup(user, group);
    }
}
