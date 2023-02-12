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
        String groupName = findGroupNameByUrl(groupUrl);
        String userName = findUserNameByEmail(email);

        return AppHeaderDto.builder()
                .groupName(groupName)
                .userName(userName)
                .build();
    }

    private String findGroupNameByUrl(String urlPath) {
        return groupService.findGroupByUrl(urlPath).getName();
    }

    private String findUserNameByEmail(String email) {
        return userRepository.findNameByEmail(email).orElseThrow();
    }

    private SideBarDto getSideBar(String userEmail, String groupUrl) {
        MemberDto member = getMemberInfo(userEmail, groupUrl);
        List<GroupViewDto> groups = getGroups(userEmail);
        List<CategoryDto> categories = getHierarchicalCategories(userEmail, groupUrl);

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
                .map(GroupViewDto::new)
                .collect(Collectors.toList());
    }

    private List<CategoryDto> getHierarchicalCategories(String email, String groupUrl) {
        return categoryService.getCategoryTree(email, groupUrl);
    }

    public Map<String, Object> getBlankHeaderAndSideBar(String email) {
        AppHeaderDto blankHeader = getBlankHeader(email);
        SideBarDto blankSideBar = getBlankSideBar(email);

        return Map.of("appHeader", blankHeader, "sideBar", blankSideBar);
    }

    public SideBarDto getBlankSideBar(String email) {
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

    public AppHeaderDto getBlankHeader(String email) {
        String username = findUserNameByEmail(email);

        return AppHeaderDto.builder()
                .userName(username)
                .groupName("그룹을 선택하세요.")
                .build();
    }

    public String getRole(String email, String groupUrl) {
        GroupDto group = groupService.findGroupByUrl(groupUrl);
        boolean isManager = group.getManagers().contains(email);

        if (isManager) return "ROLE_MANAGER";
        else return "ROLE_MEMBER";
    }
}