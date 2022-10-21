package com.huyeon.superspace.web.common.service;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import com.huyeon.superspace.web.common.dto.AppHeaderDto;
import com.huyeon.superspace.web.common.dto.SideBarDto;
import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.group.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SideBarAndHeaderService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;

    public AppHeaderDto getAppHeader(String email, String groupUrl) {
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

    public SideBarDto getSideBar(UserDetails userDetails, String groupUrl) {
        List<WorkGroup> groups = getGroups(userDetails.getUsername());
        List<CategoryDto> categories = getHierarchicalCategories(groupUrl);

        if (categories == null) categories = Collections.emptyList();

        return SideBarDto.builder()
                .groups(groups)
                .categories(categories)
                .build();
    }

    private List<WorkGroup> getGroups(String email) {
        List<UserGroup> userGroups = userGroupRepository.findGroupsByEmail(email);
        return userGroups.stream()
                .map(UserGroup::getGroup)
                .collect(Collectors.toList());
    }

    private List<CategoryDto> getHierarchicalCategories(String groupUrl) {
        WorkGroup currentGroup = getGroupByUrl(groupUrl);
        CategoryDto rootCategory = getRootOfCategoryTree(currentGroup);
        return extractRealCategory(rootCategory);
    }

    private WorkGroup getGroupByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow();
    }

    public CategoryDto getRootOfCategoryTree(WorkGroup group) {
        List<CategoryDto> categories = getCategoryList(group);

        CategoryDto rootCategoryDto = getRoot(categories);

        Map<Long, List<CategoryDto>> groupingByParent =
                groupingByParent(categories);

        addSubCategories(rootCategoryDto, groupingByParent);

        return rootCategoryDto;
    }

    private List<CategoryDto> getCategoryList(WorkGroup group) {
        return categoryRepository.findAllByGroup(group).stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    private CategoryDto getRoot(List<CategoryDto> categories) {
        return categories.get(0);
    }

    private Map<Long, List<CategoryDto>> groupingByParent(List<CategoryDto> categories) {
        return categories.stream()
                .collect(groupingBy(CategoryDto::getParentId));
    }

    private void addSubCategories(CategoryDto parent, Map<Long, List<CategoryDto>> groupingByParent) {
        List<CategoryDto> subCategories = groupingByParent.get(parent.getCategoryId());

        if (subCategories == null) return;

        parent.setSubCategories(subCategories);

        subCategories.forEach(sc -> addSubCategories(sc, groupingByParent));
    }

    private List<CategoryDto> extractRealCategory(CategoryDto rootCategory) {
        return rootCategory.getSubCategories();
    }

    public SideBarDto getBlankSideBar(String user) {
        return SideBarDto.builder()
                .groups(getGroups(user))
                .categories(List.of(new CategoryDto(-1L, "그룹을 선택하세요.", -1L)))
                .build();
    }

    public AppHeaderDto getBlankHeader(String email) {
        String username = findUserNameByEmail(email);

        return AppHeaderDto.builder()
                .userName(username)
                .groupName("그룹을 선택하세요.")
                .build();
    }
}