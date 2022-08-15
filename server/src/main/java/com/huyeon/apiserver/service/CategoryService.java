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
        List<CategoryDto> categories = getCategories(group);

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

    public List<CategoryDto> getCategories(Groups group) {
        return categoryRepository.findAllByGroup(group).stream()
                .map(c -> CategoryDto.builder()
                        .categoryId(c.getId())
                        .name(c.getName())
                        .parentId(c.getParent() == null ? 0L : c.getParent().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
