package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.group.document.Group;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.dto.CreateDto;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.JoinDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.global.exception.AlreadyExistException;
import com.huyeon.superspace.global.exception.BadRequestException;
import com.huyeon.superspace.global.exception.NotAdminException;
import com.huyeon.superspace.global.exception.NotOnlyMemberException;
import com.huyeon.superspace.domain.group.repository.NewGroupRepository;
import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.global.dto.NotyEventDto;
import com.huyeon.superspace.global.model.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewGroupService {
    private static final int IDENTIFIER_LENGTH = 10;
    private final NewGroupRepository groupRepository;
    private final NewMemberRepository memberRepository;
    private final NewCategoryService categoryService;
    private final NewBoardService boardService;
    private final ApplicationEventPublisher eventPublisher;

    public GroupDto findGroupByUrl(String url) {
        return groupRepository.findByUrl(url)
                .map(GroupDto::new)
                .orElseThrow();
    }

    private Group findGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow();
    }

    public String createGroup(String userEmail, CreateDto request) {
        Group newGroup = new Group(userEmail, request);

        //URL 중복 검사 및 구분자 추가
        if (existsByUrl(newGroup.getUrl())) {
            newGroup.setUrl(appendIdentifier(newGroup.getUrl()));
        }

        //매니저 등록 후 그룹 생성
        setMemberNum(newGroup, 1);
        registerAsManager(userEmail, newGroup);
        Group group = groupRepository.save(newGroup);

        //유저 그룹정보에 새 그룹역할 추가
        registerMyNewGroup(group, userEmail, request.getNickname(), "그룹 소유자");

        return group.getUrl();
    }

    private void registerAsManager(String userEmail, Group request) {
        Set<String> managers = request.getManagers();

        if (Objects.isNull(managers)) {
            managers = new HashSet<>();
        }

        managers.add(userEmail);
        request.setManagers(managers);
    }

    private void setMemberNum(Group request, int num) {
        request.setMembersNum(num);
    }

    private void registerMyNewGroup(Group group, String userEmail, String nickname, String role) {
        Member member = Member.builder()
                .group(group)
                .userEmail(userEmail)
                .nickname(nickname)
                .role(role)
                .build();

        memberRepository.save(member);
    }

    public List<GroupDto> getMyGroups(String userEmail) {
        List<Member> groups = findAllByUserEmail(userEmail);

        return groups.stream()
                .map(group -> findGroupById(group.getGroup().getId()))
                .map(GroupDto::new)
                .collect(Collectors.toList());
    }

    public List<Member> findAllByUserEmail(String userEmail) {
        return memberRepository.findAllByUserEmail(userEmail);
    }

    private boolean existsByUrl(String url) {
        return groupRepository.existsByUrl(url);
    }

    private String appendIdentifier(String urlPath) {
        return urlPath + "-" + generateIdentifier();
    }

    private String generateIdentifier() {
        int leftLimit = '0'; // numeral '0'
        int rightLimit = 'z'; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(IDENTIFIER_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String editGroup(String url, GroupDto request) {
        GroupDto group = findGroupByUrl(url);
        group.setName(request.getName());
        group.setUrl(request.getUrl());
        group.setDescription(request.getDescription());
        return groupRepository.save(new Group(group)).getUrl();
    }

    public void deleteGroup(String userEmail, String url) {
        GroupDto group = findGroupByUrl(url);
        if (isNotGroupOwner(userEmail, group)) {
            throw new NotAdminException("그룹 소유자가 아닙니다.");
        }

        if (isNotOnlyMember(group)) {
            throw new NotOnlyMemberException("다른 멤버가 존재합니다.");
        }

        groupRepository.deleteById(group.getId());
        deleteMemberInGroup(group.getId(), userEmail);
        categoryService.deleteAllByGroupUrl(group.getUrl());
        boardService.deleteAllByGroupUrl(group.getUrl());
    }

    private boolean isNotGroupOwner(String email, GroupDto group) {
        return !group.getOwner().equals(email);
    }

    private boolean isNotOnlyMember(GroupDto group) {
        return group.getMembersNum() != 1;
    }

    private void deleteMemberInGroup(String groupId, String userEmail) {
        memberRepository.deleteByGroupIdAndUserEmail(groupId, userEmail);
    }

    public void saveMemberRole(String groupUrl, List<MemberDto> request) {
        GroupDto group = findGroupByUrl(groupUrl);

        for (MemberDto member : request) {
            String currentRole = findRoleByEmail(group, member.getEmail());
            String changedRole = member.getRole();

            if (isChanged(currentRole, changedRole)) {
                //일반 멤버 -> 관리자
                if (changedRole.equals("ROLE_MANAGER")) {
                    registerUserAsManager(member, group);
                    continue;
                }
                //관리자 -> 일반 멤버
                if (changedRole.equals("ROLE_MEMBER")) {
                    revokeManager(member, group);
                }
            }
        }
    }

    private void revokeManager(MemberDto member, GroupDto group) {
        group.getManagers().remove(member.getEmail());
        groupRepository.save(new Group(group));
    }

    private void registerUserAsManager(MemberDto member, GroupDto group) {
        group.getManagers().add(member.getEmail());
        groupRepository.save(new Group(group));
    }

    public String findRoleByEmail(GroupDto group, String userEmail) {
        for (String next : group.getManagers()) {
            if (next.equals(userEmail)) {
                return "ROLE_MANAGER";
            }
        }

        return "ROLE_MEMBER";
    }

    private boolean isChanged(String currentRole, String changedRole) {
        return !currentRole.equals(changedRole);
    }

    public void expelMember(String userEmail, String groupUrl, String request) {
        GroupDto group = findGroupByUrl(groupUrl);
        //권한 체크
        checkManagerRole(group, userEmail);

        //추방가능한 멤버인지 확인
        checkNotManagerRole(group, request);

        //강퇴
        deleteMemberInGroup(group.getId(), userEmail);

        //TODO: 탈퇴한 멤버가 작성한 게시글/댓글 작성자를 '탈퇴한 멤버'로 바꾸기
    }

    private void checkManagerRole(GroupDto group, String userEmail) {
        String role = findRoleByEmail(group, userEmail);

        if (!role.equals("ROLE_MANAGER")) {
            throw new NotAdminException("그룹 관리자만 멤버를 강퇴할 수 있습니다.");
        }
    }

    private void checkNotManagerRole(GroupDto group, String userEmail) {
        String role = findRoleByEmail(group, userEmail);

        if (role.equals("ROLE_MANAGER")) {
            throw new BadRequestException("그룹 관리자 멤버는 강퇴할 수 없습니다.");
        }
    }

    public void inviteMember(String groupUrl, String userEmail) {
        GroupDto group = findGroupByUrl(groupUrl);

        Noty inviteNoty = Noty.builder()
                .senderName(group.getName())
                .message("그룹에 초대합니다!\n그룹 설명: " + group.getDescription())
                .type(NotyType.GROUP_INVITE)
                .redirectUrl(group.getUrl())
                .payload(group.getId())
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

    public void joinMember(String userEmail, JoinDto request) {
        Group group = findGroupById(request.getGroupId());
        if (isMember(request.getGroupId(), userEmail)) {
            throw new AlreadyExistException("이미 그룹에 가입되어 있습니다.");
        }

        registerMyNewGroup(group, userEmail, request.getNickname(), "일반 멤버");
        setMemberNum(group, group.getMembersNum() + 1);
    }

    private boolean isMember(String groupId, String userEmail) {
        return memberRepository.existsByGroupIdAndUserEmail(groupId, userEmail);
    }

    public boolean isNotMemberByUrl(String groupUrl, String userEmail) {
        String groupId = findGroupByUrl(groupUrl).getId();
        return !isMember(groupId, userEmail);
    }

    public List<MemberDto> findMembersById(String groupId) {
        return memberRepository.findAllByGroupId(groupId).stream()
                .map(MemberDto::new)
                .collect(Collectors.toList());
    }

    public boolean isNotManager(String groupUrl, String userEmail) {
        Set<String> managers = findGroupByUrl(groupUrl).getManagers();
        return !managers.contains(userEmail);
    }

    public void resignGroup(String url, String userEmail) {
        GroupDto group = findGroupByUrl(url);
        //탈퇴 가능한 그룹인지 확인
        if (!isMember(group.getId(), userEmail)) {
            throw new BadRequestException("해당 그룹 멤버가 아닙니다!");
        }
        //강퇴
        deleteMemberInGroup(group.getId(), userEmail);
    }
}
