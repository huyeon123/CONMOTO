package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.UserDetailsImpl;
import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.dto.HeaderDto;
import com.huyeon.apiserver.model.dto.SideBarDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SideBarAndHeaderService {

    private final GroupService groupService;
    private final CategoryService categoryService;

    public HeaderDto getAppHeader(UserDetailsImpl userDetails, String groupUrl) {
        String groupName = groupService.getGroupNameByUrl(groupUrl);
        String userName = userDetails.getUser().getName();

        return HeaderDto.builder()
                .groupName(groupName)
                .userName(userName)
                .build();
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
        return groupService.getGroups(user);
    }

    private List<CategoryDto> getHierarchicalCategories(String groupUrl) {
        WorkGroup currentGroup = groupService.getGroupByUrl(groupUrl);
        CategoryDto rootCategory = categoryService.getRootOfCategoryTree(currentGroup);
        return extractRealCategory(rootCategory);
    }

    private List<CategoryDto> extractRealCategory(CategoryDto rootCategory) {
        return rootCategory.getSubCategories();
    }

    public SideBarDto getBlankSideBar(User user) {
        return SideBarDto.builder()
                .groups(groupService.getGroups(user))
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