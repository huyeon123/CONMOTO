package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.dto.HeaderDto;
import com.huyeon.apiserver.model.dto.SideBarDto;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.UserGroup;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;

    public HeaderDto getAppHeader(User user, String groupUrl) {
        String groupName = findGroupNameByUrl(groupUrl);
        String userName = user.getName();

        return HeaderDto.builder()
                .groupName(groupName)
                .userName(userName)
                .build();
    }

    private String findGroupNameByUrl(String urlPath) {
        return groupRepository.findNameByUrl(urlPath).orElseThrow();
    }

    public SideBarDto getSideBar(UserDetailsImpl userDetails, String groupUrl) {
        List<WorkGroup> groups = getGroups(userDetails.getUser());
        List<CategoryDto> categories = getHierarchicalCategories(groupUrl);

        if (categories == null) categories = Collections.emptyList();

        return SideBarDto.builder()
                .groups(groups)
                .categories(categories)
                .build();
    }

    private List<WorkGroup> getGroups(User user) {
        List<UserGroup> userGroups = userGroupRepository.findAllByUser(user);
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

    public SideBarDto getBlankSideBar(User user) {
        return SideBarDto.builder()
                .groups(getGroups(user))
                .categories(List.of(new CategoryDto(-1L, "그룹을 선택하세요.", -1L)))
                .build();
    }

    public HeaderDto getBlankHeader(User user) {
        return HeaderDto.builder()
                .userName(user.getName())
                .groupName("그룹을 선택하세요.")
                .build();
    }
}