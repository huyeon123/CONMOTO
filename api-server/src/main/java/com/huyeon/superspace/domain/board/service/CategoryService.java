package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
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
        List<CategoryDto> originalList = getCategoryList(group);

        IntStream.range(0, request.size())
                .forEach(i -> {
                    CategoryDto origin = originalList.get(i + 1);
                    CategoryDto change = request.get(i);

                    changeInfoIfDifferent(originalList, origin, change);

                });
    }

    private void changeInfoIfDifferent(List<CategoryDto> originalList, CategoryDto origin, CategoryDto change) {
        boolean nameChanged = isNameChanged(origin, change);
        boolean parentChanged = isParentChanged(originalList, origin, change);

        if (nameChanged || parentChanged) {
            Category category = categoryRepository.findById(origin.getCategoryId()).orElseThrow();
            if (nameChanged) {
                changeName(category, change);
            }

            if (parentChanged) {
                changeParent(originalList, category, change);
            }
        }
    }

    private boolean isNameChanged(CategoryDto origin, CategoryDto change) {
        return !origin.getName().equals(change.getName());
    }

    private boolean isParentChanged(List<CategoryDto> originalList, CategoryDto origin, CategoryDto change) {
        Long parentId = getParentId(originalList, change);
        return !origin.getParentId().equals(parentId);
    }

    private Long getParentId(List<CategoryDto> originalList, CategoryDto change) {
        return originalList.get(change.getParentOrder()).getCategoryId();
    }

    private void changeName(Category origin, CategoryDto change) {
        origin.setName(change.getName());
        categoryRepository.save(origin);
    }

    private void changeParent(List<CategoryDto> originalList, Category origin, CategoryDto change) {
        Long parentId = getParentId(originalList, change);
        Category parent = categoryRepository.findById(parentId).orElseThrow();
        origin.setParent(parent);
        categoryRepository.save(origin);
    }

    @Transactional(readOnly = true)
    public Category getCategory(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
