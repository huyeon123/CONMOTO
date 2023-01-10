package com.huyeon.superspace.web.common.service;

import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.repository.GroupManagerRepository;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.group.repository.UserGroupRepository;
import com.huyeon.superspace.domain.newboard.dto.CategoryDto;
import com.huyeon.superspace.domain.newboard.service.NewCategoryService;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.global.support.CacheUtils;
import com.huyeon.superspace.web.common.dto.AppHeaderDto;
import com.huyeon.superspace.web.common.dto.SideBarDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SideBarAndHeaderService {

    private static final String CACHE_KEY_TYPE = "HSB"; //Header and SideBar

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final GroupManagerRepository managerRepository;
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
        return groupRepository.findNameByUrl(urlPath).orElseThrow();
    }

    private String findUserNameByEmail(String email) {
        return userRepository.findNameByEmail(email).orElseThrow();
    }

    private SideBarDto getSideBar(String userEmail, String groupUrl) {
        List<GroupDto> groups = getGroups(userEmail);
        List<CategoryDto> categories = getHierarchicalCategories(groupUrl);
        addUserRole(groups, userEmail);

        if (categories == null) categories = Collections.emptyList();

        return SideBarDto.builder()
                .groups(groups)
                .categories(categories)
                .build();
    }

    private List<GroupDto> getGroups(String email) {
        List<UserGroup> userGroups = userGroupRepository.findGroupsByEmail(email);
        return userGroups.stream()
                .map(UserGroup::getGroup)
                .map(GroupDto::new)
                .collect(toList());
    }

    private List<com.huyeon.superspace.domain.newboard.dto.CategoryDto> getHierarchicalCategories(String groupUrl) {
        return categoryService.getCategoryTree(groupUrl);
    }

    private void addUserRole(List<GroupDto> groups, String userEmail) {
        for (GroupDto group : groups) {
            String groupUrl = group.getUrl();
            String role = getRole(userEmail, groupUrl);
            group.setRole(role);
        }
    }

    public Map<String, Object> getBlankHeaderAndSideBar(String email) {
        AppHeaderDto blankHeader = getBlankHeader(email);
        SideBarDto blankSideBar = getBlankSideBar(email);

        return Map.of("appHeader", blankHeader, "sideBar", blankSideBar);
    }

    public SideBarDto getBlankSideBar(String email) {
        List<GroupDto> groups = getGroups(email);
        addUserRole(groups, email);

        return SideBarDto.builder()
                .groups(groups)
                .categories(List.of())
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
        boolean isManager = managerRepository.existsByEmailAndUrl(email, groupUrl);

        if (isManager) return "ROLE_MANAGER";
        else return "ROLE_MEMBER";
    }
}