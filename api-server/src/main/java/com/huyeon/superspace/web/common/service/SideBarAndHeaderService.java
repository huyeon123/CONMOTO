package com.huyeon.superspace.web.common.service;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.service.NewCategoryService;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.GroupViewDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.service.NewGroupService;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.global.support.CacheUtils;
import com.huyeon.superspace.web.common.dto.AppHeaderDto;
import com.huyeon.superspace.web.common.dto.SideBarDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SideBarAndHeaderService {

    private static final String CACHE_KEY_TYPE = "HSB"; //Header and SideBar

    private final UserRepository userRepository;
    private final NewGroupService groupService;
    private final NewCategoryService categoryService;
    private final CacheUtils cacheUtils;

    public Map<String, Object> getHeaderAndSideBar(String email, String groupUrl) {
        //email이 CONMOTO_ANONYMOUS_TOKEN일 경우 익명 데이터
        if (email.equals("CONMOTO_ANONYMOUS_TOKEN")) {
            return getAnonymousHSB(email, groupUrl);
        }

        //로그인은 했으나 헤더 사이드바 정보가 필요없다면 기본 데이터
        if (groupUrl == null) {
            return getDefaultHSB(email);
        }

        return getFullHSB(email, groupUrl);
    }

    private Map<String, Object> getAnonymousHSB(String email, String groupUrl) {
        AppHeaderDto appHeader = getAnonymousHeader(groupUrl);
        SideBarDto sideBar = getAnonymousSideBar(email, groupUrl);

        return Map.of(
                "appHeader", appHeader,
                "sideBar", sideBar,
                "role", "ROLE_ANONYMOUS"
        );
    }

    private AppHeaderDto getAnonymousHeader(String groupUrl) {
        String groupName;
        if (Objects.isNull(groupUrl)) groupName = "그룹을 선택하세요.";
        else groupName = getGroupNameByUrl(groupUrl);

        return new AppHeaderDto(groupName, "로그인");
    }

    private SideBarDto getAnonymousSideBar(String email, String groupUrl) {
        MemberDto member = getAnonymousMember(email);
        List<CategoryDto> categories = getMyCategoryList(email, groupUrl);
        return SideBarDto.builder()
                .member(member)
                .groups(List.of())
                .categories(categories)
                .build();
    }

    private MemberDto getAnonymousMember(String email) {
        return MemberDto.builder()
                .id("anonymous")
                .email(email)
                .nickname("로그인이 필요합니다.")
                .build();
    }

    private Map<String, Object> getFullHSB(String email, String groupUrl) {
        String key = String.format("%s:%s:%s", CACHE_KEY_TYPE, email, groupUrl);
        Map<String, Object> cache = cacheUtils.findCache(key);

        if (!cache.isEmpty()) return cache;

        AppHeaderDto appHeader = getAppHeader(email, groupUrl);
        SideBarDto sideBar = getSideBar(email, groupUrl);
        String role = getRole(email, groupUrl);

        Map<String, Object> value = Map.of(
                "appHeader", appHeader,
                "sideBar", sideBar,
                "role", role
        );

        cacheUtils.saveCache(key, value);
        cacheUtils.setExpire(key, 1, TimeUnit.MINUTES);

        return value;
    }

    private AppHeaderDto getAppHeader(String email, String groupUrl) {
        String groupName = getGroupNameByUrl(groupUrl);
        String userName = getUserNameByEmail(email);

        return AppHeaderDto.builder()
                .groupName(groupName)
                .userName(userName)
                .build();
    }

    private String getGroupNameByUrl(String urlPath) {
        return groupService.getGroupByUrl(urlPath).getName();
    }

    private String getUserNameByEmail(String email) {
        return userRepository.findNameByEmail(email).orElseThrow();
    }

    private SideBarDto getSideBar(String userEmail, String groupUrl) {
        MemberDto member = getMemberInfo(userEmail, groupUrl);
        List<GroupViewDto> groups = getGroups(userEmail);
        List<CategoryDto> categories = getMyCategoryList(userEmail, groupUrl);

        if (categories == null) categories = Collections.emptyList();

        return SideBarDto.builder()
                .member(member)
                .groups(groups)
                .categories(categories)
                .build();
    }

    private MemberDto getMemberInfo(String userEmail, String groupUrl) {
        return groupService.findByUserEmail(userEmail, groupUrl);
    }

    private List<GroupViewDto> getGroups(String email) {
        List<Member> groups = groupService.findAllByUserEmail(email);

        return groups.stream()
                .map(member -> {
                    GroupDto group = groupService.getGroupByUrl(member.getGroupUrl());
                    return new GroupViewDto(group, member);
                })
                .collect(Collectors.toList());
    }

    private List<CategoryDto> getMyCategoryList(String email, String groupUrl) {
        return categoryService.getMyCategoryList(email, groupUrl);
    }

    private Map<String, Object> getDefaultHSB(String email) {
        AppHeaderDto blankHeader = getBlankHeader(email);
        SideBarDto blankSideBar = getBlankSideBar(email);

        return Map.of("appHeader", blankHeader, "sideBar", blankSideBar);
    }

    private SideBarDto getBlankSideBar(String email) {
        MemberDto member = getNoneSelectGroupProfile();
        List<GroupViewDto> groups = getGroups(email);
        return SideBarDto.builder()
                .member(member)
                .groups(groups)
                .categories(List.of())
                .build();
    }

    private MemberDto getNoneSelectGroupProfile() {
        return MemberDto.builder()
                .id("anonymous")
                .nickname("선택한 그룹이 없습니다.")
                .build();
    }

    private AppHeaderDto getBlankHeader(String email) {
        String username = getUserNameByEmail(email);

        return AppHeaderDto.builder()
                .userName(username)
                .groupName("그룹을 선택하세요.")
                .build();
    }

    public String getRole(String email, String groupUrl) {
        boolean notMember = groupService.isNotMemberByUrl(groupUrl, email);
        if (notMember) return "ROLE_NOT_MEMBER";

        GroupDto group = groupService.getGroupByUrl(groupUrl);
        boolean isManager = group.getManagers().contains(email);

        if (isManager) return "ROLE_MANAGER";
        else return "ROLE_MEMBER";
    }
}