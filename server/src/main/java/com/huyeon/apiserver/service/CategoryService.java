package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public boolean createCategory(CategoryDto request, Groups group) {
        Category parentCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        Category newCategory = Category.builder()
                .name(request.getName())
                .parent(parentCategory)
                .group(group)
                .build();

        categoryRepository.save(newCategory);
        return true;
    }

    public void createRootCategory(Groups group) {
        Category root = Category.builder()
                .name("==최상위 카테고리==")
                .parent(null)
                .group(group)
                .build();

        categoryRepository.save(root);
    }

    public CategoryDto getRootOfCategoryTree(Groups group) {
        List<CategoryDto> categories = getCategoryList(group);

        CategoryDto rootCategoryDto = getRoot(categories);

        Map<Long, List<CategoryDto>> groupingByParent =
                groupingByParent(categories);
        addSubCategories(rootCategoryDto, groupingByParent);

        return rootCategoryDto;
    }

    private Map<Long, List<CategoryDto>> groupingByParent(List<CategoryDto> categories) {
        return categories.stream().collect(groupingBy(CategoryDto::getParentId));
    }

    private CategoryDto getRoot(List<CategoryDto> categories) {
        return categories.get(0);
    }

    private void addSubCategories(CategoryDto parent, Map<Long, List<CategoryDto>> groupingByParent) {
        List<CategoryDto> subCategories = groupingByParent.get(parent.getCategoryId());

        if (subCategories == null) return;

        parent.setSubCategories(subCategories);

        subCategories.forEach(sc -> addSubCategories(sc, groupingByParent));
    }

    public List<CategoryDto> getCategoryList(Groups group) {
        return categoryRepository.findAllByGroup(group).stream()
                .map(c -> CategoryDto.builder()
                        .categoryId(c.getId())
                        .name(c.getName())
                        .parentId(c.getParent() == null ? 0L : c.getParent().getId())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean editCategory(List<CategoryDto> request, Groups group) {
        List<CategoryDto> beforeList = getCategoryList(group);

        IntStream.range(0, request.size())
                .forEach(i -> {
                    CategoryDto before = beforeList.get(i + 1);
                    CategoryDto after = request.get(i);

                    changeInfoIfDifferent(beforeList, before, after);

                });
        return true;
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
