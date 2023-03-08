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

    public GroupDto getGroupByUrl(String url) {
        return groupRepository.findByUrl(url)
                .map(GroupDto::new)
                .orElseThrow();
    }

    public Group findGroupByUrl(String url) {
        return groupRepository.findByUrl(url)
                .orElseThrow();
    }

    private Grade findGradeByUrl(String groupUrl) {
        return gradeRepository.findByGroupUrl(groupUrl).orElseThrow();
    }

    public String createGroup(String userEmail, CreateDto request) {
        Group newGroup = new Group(userEmail, request);

        //URL 중복 검사 및 구분자 추가
        if (existsByUrl(newGroup.getUrl())) {
            throw new BadRequestException("중복된 그룹 URL 입니다.");
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
                .groupUrl(group.getUrl())
                .userEmail(userEmail)
                .nickname(nickname)
                .role(role)
                .build();

        memberRepository.save(member);
    }

    public List<GroupDto> getMyGroups(String userEmail) {
        List<Member> groups = findAllByUserEmail(userEmail);

        return groups.stream()
                .map(group -> getGroupByUrl(group.getGroupUrl()))
                .collect(Collectors.toList());
    }

    public List<Member> findAllByUserEmail(String userEmail) {
        return memberRepository.findAllByUserEmail(userEmail);
    }

    public MemberDto findByUserEmail(String userEmail, String groupUrl) {
        Grade grade = findGradeByUrl(groupUrl);

        return memberRepository.findByGroupUrlAndUserEmail(groupUrl, userEmail)
                .map(member -> {
                    String levelName = grade.getLevelName(member.getGradeLevel());
                    return new MemberDto(member, levelName);
                })
                .orElse(MemberDto.builder()
                        .id("anonymous")
                        .nickname("그룹 가입이 필요합니다.")
                        .build());
    }

    private boolean existsByUrl(String url) {
        return groupRepository.existsByUrl(url);
    }

    public String editGroup(String url, String type, GroupDto request) {
        Group group = groupRepository.findByUrl(url).orElseThrow();
        modifyGroupInfo(type, group, request);
        return groupRepository.save(group).getUrl();
    }

    private void modifyGroupInfo(String type, Group origin, GroupDto request) {
        switch (type) {
            case "name":
                origin.setName(request.getName());
                break;
            case "description":
                origin.setDescription(request.getDescription());
                break;
            case "open":
                origin.setOpen(request.isOpen());
                break;
            case "autoJoin":
                origin.setAutoJoin(request.isAutoJoin());
                break;
        }
    }

    public void deleteGroup(String userEmail, String url) {
        GroupDto group = getGroupByUrl(url);
        if (isNotGroupOwner(userEmail, group)) {
            throw new NotAdminException("그룹 소유자가 아닙니다.");
        }

        if (isNotOnlyMember(group)) {
            throw new NotOnlyMemberException("다른 멤버가 존재합니다.");
        }

        groupRepository.deleteById(group.getId());
        deleteMemberInGroup(group.getUrl(), userEmail);
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
        memberRepository.deleteByGroupUrlAndUserEmail(groupId, userEmail);
    }

    public String findRoleByEmail(GroupDto group, String userEmail) {
        for (String next : group.getManagers()) {
            if (next.equals(userEmail)) {
                return "ROLE_MANAGER";
            }
        }

        return "ROLE_MEMBER";
    }

    public void expelMember(String userEmail, String groupUrl, ExpelDto request) {
        GroupDto group = getGroupByUrl(groupUrl);
        //권한 체크
        checkManagerRole(group, userEmail);

        //추방가능한 멤버인지 확인
        checkNotManagerRole(group, request.getEmail());

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

    public void inviteMember(String groupUrl, String requester, String inviter) {
        GroupDto group = getGroupByUrl(groupUrl);

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
        return memberRepository.existsByGroupUrlAndUserEmail(groupId, userEmail);
    }

    public boolean isNotMemberByUrl(String groupUrl, String userEmail) {
        return !isMember(groupUrl, userEmail);
    }

    public List<MemberDto> getMembersById(String groupUrl) {
        Grade grade = findGradeByUrl(groupUrl);

        return memberRepository.findAllByGroupUrl(groupUrl).stream()
                .map(member -> new MemberDto(member, grade.getLevelName(member.getGradeLevel())))
                .collect(Collectors.toList());
    }

    public boolean isNotManager(String groupUrl, String userEmail) {
        Set<String> managers = getGroupByUrl(groupUrl).getManagers();
        return !managers.contains(userEmail);
    }

    public void resignGroup(String url, String userEmail) {
        GroupDto group = getGroupByUrl(url);
        //탈퇴 가능한 그룹인지 확인
        if (!isMember(url, userEmail)) {
            throw new BadRequestException("해당 그룹 멤버가 아닙니다!");
        }
        //강퇴
        deleteMemberInGroup(group.getId(), userEmail);
    }
}
