package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final GroupService groupService;
    private final GroupManagerService managerService;
    private final UserRepository userRepository;

    public boolean saveMemberRole(WorkGroup group, List<MemberDto> request) {
        for (MemberDto member : request) {
            User user = userRepository.findByEmail(member.getEmail()).orElseThrow();
            String currentRole = groupService.checkRole(group, user);
            String changedRole = member.getGroupRole();

            if (isChanged(currentRole, changedRole)) {
                //일반 멤버 -> 관리자
                if (changedRole.equals("그룹 관리자")) {
                    managerService.registerUserAsManager(user, group);
                }
                //관리자 -> 일반 멤버
                if (changedRole.equals("일반 멤버")) {
                    managerService.revokeManager(user, group);
                }
            }
        }
        return true;
    }

    private void applyChange(User user, WorkGroup group) {

    }

    private boolean isChanged(String currentRole, String changedRole) {
        return !currentRole.equals(changedRole);
    }
}
