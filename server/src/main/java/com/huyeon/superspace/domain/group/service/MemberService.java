package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.entity.GroupManager;
import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.exception.AlreadyExistException;
import com.huyeon.superspace.domain.group.repository.GroupManagerRepository;
import com.huyeon.superspace.domain.group.repository.UserGroupRepository;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.global.dto.NotyEventDto;
import com.huyeon.superspace.global.model.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final GroupService groupService;
    private final GroupManagerRepository managerRepository;
    private final UserGroupRepository userGroupRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void inviteMember(String groupUrl, String userEmail) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);

        Noty inviteNoty = Noty.builder()
                .senderName(group.getName())
                .message("그룹에 초대합니다!\n" + group.getDescription())
                .type(NotyType.GROUP_INVITE)
                .redirectUrl(group.getUrlPath())
                .build();

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .userEmail(userEmail)
                .noty(inviteNoty)
                .build();

        NotyEventDto notyEventDto = NotyEventDto.builder()
                .noty(inviteNoty)
                .receivers(List.of(receivedNoty))
                .build();

        EventPublisher.publishEvent(eventPublisher, notyEventDto);
    }

    public void joinMember(String email, String groupUrl) {
        User user = groupService.findUserByEmail(email);
        WorkGroup group = groupService.getGroupByUrl(groupUrl);

        if (isExist(user, group)) {
            throw new AlreadyExistException("이미 그룹에 가입되어 있습니다.");
        }

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(group)
                .build();

        userGroupRepository.save(userGroup);
    }

    private boolean isExist(User user, WorkGroup group) {
        return userGroupRepository.existsByUserAndGroup(user, group);
    }

    public void saveMemberRole(String groupUrl, List<MemberDto> request) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);

        for (MemberDto member : request) {
            User user = groupService.findUserByEmail(member.getEmail());
            String currentRole = groupService.checkRole(group, user);
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

    private boolean isChanged(String currentRole, String changedRole) {
        return !currentRole.equals(changedRole);
    }

    private void registerUserAsManager(User user, WorkGroup group) {
        GroupManager groupManager = GroupManager.builder()
                .group(group)
                .manager(user)
                .build();

        managerRepository.save(groupManager);
    }

    private void revokeManager(User user, WorkGroup group) {
        managerRepository.deleteByGroupAndManager(group, user);
    }

    public void expelUser(String groupUrl, MemberDto request) {
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        User user = groupService.findUserByEmail(request.getEmail());
        userGroupRepository.deleteByUserAndGroup(user, group);
    }
}
