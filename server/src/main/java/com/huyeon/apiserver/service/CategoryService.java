package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;

    public void createCategory(CategoryDto request, String groupUrl) {
        WorkGroup group = findGroupByUrl(groupUrl);

        Category parentCategory = findCategoryById(request.getCategoryId()).orElseThrow();

        Category newCategory = Category.builder()
                .name(request.getName())
                .parent(parentCategory)
                .group(group)
                .build();

        save(newCategory);
    }

    private WorkGroup findGroupByUrl(String urlPath) {
        return groupRepository.findByUrlPath(urlPath).orElseThrow();
    }

    private Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    private void save(Category category) {
        categoryRepository.save(category);
    }

    public CategoryDto getRootOfCategoryTree(WorkGroup group) {
        List<CategoryDto> categories = getCategoryList(group);

        CategoryDto rootCategoryDto = getRoot(categories);

        Map<Long, List<CategoryDto>> groupingByParent =
                groupingByParent(categories);

        addSubCategories(rootCategoryDto, groupingByParent);

        return rootCategoryDto;
    }

    public List<CategoryDto> getCategoryList(WorkGroup group) {
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

    public void editCategory(List<CategoryDto> request, String groupUrl) {
        WorkGroup group = findGroupByUrl(groupUrl);
        List<CategoryDto> beforeList = getCategoryList(group);

        IntStream.range(0, request.size())
                .forEach(i -> {
                    CategoryDto before = beforeList.get(i + 1);
                    CategoryDto after = request.get(i);

                    changeInfoIfDifferent(beforeList, before, after);

                });
    }

    private void changeInfoIfDifferent(List<CategoryDto> beforeList, CategoryDto before, CategoryDto after) {
        boolean nameChanged = isNameChanged(before, after);
        boolean parentChanged = isParentChanged(beforeList, before, after);
        if (nameChanged || parentChanged) {
            Category category = categoryRepository.findById(before.getCategoryId()).orElseThrow();
            if (nameChanged) {
                changeName(category, after);
            }

            if (parentChanged) {
                changeParent(beforeList, category, after);
            }
        }
    }

    private boolean isNameChanged(CategoryDto before, CategoryDto after) {
        return !before.getName().equals(after.getName());
    }

    private boolean isParentChanged(List<CategoryDto> beforeList, CategoryDto before, CategoryDto after) {
        Long parentId = getParentId(beforeList, after);
        return !before.getParentId().equals(parentId);
    }

    private void changeName(Category before, CategoryDto after) {
        before.setName(after.getName());
        categoryRepository.save(before);
    }

    private void changeParent(List<CategoryDto> beforeList, Category before, CategoryDto after) {
        Long parentId = getParentId(beforeList, after);
        Category parent = categoryRepository.findById(parentId).orElseThrow();
        before.setParent(parent);
        categoryRepository.save(before);
    }

    private Long getParentId(List<CategoryDto> beforeList, CategoryDto after) {
        return beforeList.get(after.getParentId().intValue()).getCategoryId();
    }

    public Category getCategory(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
