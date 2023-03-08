package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.board.service.NewBoardService;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import com.huyeon.superspace.domain.group.document.Grade;
import com.huyeon.superspace.domain.group.document.Group;
import com.huyeon.superspace.domain.group.document.JoinRequest;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.dto.*;
import com.huyeon.superspace.domain.group.repository.GradeRepository;
import com.huyeon.superspace.domain.group.repository.JoinRequestRepository;
import com.huyeon.superspace.domain.group.repository.NewGroupRepository;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import com.huyeon.superspace.domain.noty.dto.NotyPayloadDto;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.service.NotyUtils;
import com.huyeon.superspace.global.exception.AlreadyExistException;
import com.huyeon.superspace.global.exception.BadRequestException;
import com.huyeon.superspace.global.exception.NotAdminException;
import com.huyeon.superspace.global.exception.NotOnlyMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewGroupService {
    private final NewGroupRepository groupRepository;
    private final NewMemberRepository memberRepository;

    private final GradeRepository gradeRepository;

    private final JoinRequestRepository joinRequestRepository;

    private final NewCategoryService categoryService;
    private final NewBoardService boardService;
    private final NotyUtils notyUtils;

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

        //URL 중복 검사
        if (existsByUrl(newGroup.getUrl())) {
            throw new BadRequestException("중복된 그룹 URL 입니다.");
        }

        //매니저 등록 후 그룹 생성
        setMemberNum(newGroup, 1);
        registerAsManager(userEmail, newGroup);
        Group group = groupRepository.save(newGroup);

        //공지사항 카테고리 생성
        categoryService.createNotice(group.getUrl(), group.getName());

        //등급 생성
        saveDefaultGradeInfo(group.getUrl());

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
                .gradeLevel(3)
                .build();

        memberRepository.save(member);
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

        CompletableFuture.runAsync(() -> {
            NotyPayloadDto payload = NotyPayloadDto.builder()
                    .type(NotyType.GROUP_EXPEL)
                    .group(group)
                    .data(request.getReason())
                    .receiverEmail(request.getEmail())
                    .build();

            notyUtils.publishNoty(payload);
        });
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

        CompletableFuture.runAsync(() -> {
            NotyPayloadDto payload = NotyPayloadDto.builder()
                    .type(NotyType.GROUP_INVITE)
                    .group(group)
                    .requesterEmail(requester)
                    .receiverEmail(inviter).build();

            notyUtils.publishNoty(payload);
        });
    }

    public boolean joinMember(String userEmail, JoinDto request) {
        Group group = findGroupByUrl(request.getGroupUrl());

        if (isMember(group.getUrl(), userEmail)) {
            throw new AlreadyExistException("이미 그룹에 가입되어 있습니다.");
        }

        if (group.isAutoJoin()) {
            //즉시 가입 처리
            registerMyNewGroup(group, userEmail, request.getNickname(), "일반 멤버");
            setMemberNum(group, group.getMembersNum() + 1);
            groupRepository.save(group);
            return true;
        } else {
            //가입 요청 목록에 추가
            JoinRequest joinRequest = joinRequestRepository.findByGroupUrl(request.getGroupUrl())
                    .orElse(new JoinRequest(request.getGroupUrl()));

            joinRequest.addRequester(request);

            joinRequestRepository.save(joinRequest);
            return false;
        }
    }

    public void acceptMember(JoinRequestDto request) {
        Group group = findGroupByUrl(request.getGroupUrl());
        JoinRequestDto joinRequestList = getJoinRequestList(request.getGroupUrl());

        AtomicInteger success = new AtomicInteger();
        request.getRequesters().forEach(requester -> {
            //그룹 등록
            String userEmail = requester.getUserEmail();
            String nickname = requester.getNickname();
            registerMyNewGroup(group, userEmail, nickname, "일반 멤버");
            //신청 목록에서 제거
            joinRequestList.removeRequester(requester);
            success.getAndIncrement();
        });

        //가입 신청 목록 업데이트
        joinRequestRepository.save(new JoinRequest(joinRequestList));

        //멤버 수 증가
        setMemberNum(group, group.getMembersNum() + success.get());
        groupRepository.save(group);
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

    public MemberDto getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .map(member -> {
                    Grade grade = findGradeByUrl(member.getGroupUrl());
                    String levelName = grade.getLevelName(member.getGradeLevel());
                    return new MemberDto(member, levelName);
                })
                .orElseThrow();
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

    public GradeDto getGroupGrade(String url) {
        Grade grade = gradeRepository.findByGroupUrl(url)
                .orElseThrow();

        return new GradeDto(grade);
    }

    public void saveGradeInfo(GradeDto request) {
        Optional<Grade> optional = gradeRepository.findByGroupUrl(request.getGroupUrl());
        optional.ifPresent(value -> request.setId(value.getId()));
        gradeRepository.save(new Grade(request));
    }

    private void saveDefaultGradeInfo(String groupUrl) {
        gradeRepository.save(new Grade(groupUrl));
    }

    public JoinRequestDto getJoinRequestList(String groupUrl) {
        JoinRequest joinRequest = joinRequestRepository.findByGroupUrl(groupUrl)
                .orElse(new JoinRequest(groupUrl));

        return new JoinRequestDto(joinRequest);
    }

    public void adjustMemberGrade(String memberId, int level) {
        Member member = findMemberById(memberId);
        member.setGradeLevel(level);
        memberRepository.save(member);

        CompletableFuture.runAsync(() -> createGradeChangePayload(member, level));
    }

    private void createGradeChangePayload(Member member, int level) {
        GroupDto group = getGroupByUrl(member.getGroupUrl());
        String levelName = findGradeByUrl(member.getGroupUrl())
                .getLevelName(level);
        NotyPayloadDto payload = NotyPayloadDto.builder()
                .type(NotyType.GROUP_ROLE_CHANGE)
                .group(group)
                .data(levelName)
                .receiverEmail(member.getUserEmail())
                .build();
        notyUtils.publishNoty(payload);
    }

    private Member findMemberById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow();
    }
}
