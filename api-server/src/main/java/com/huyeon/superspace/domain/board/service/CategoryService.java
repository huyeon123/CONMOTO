package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
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

        Category newCategory = Category.builder()
                .name(request.getName())
                .group(group)
                .build();

        if (request.getParentId() != null) {
            Category parentCategory = findCategoryById(request.getParentId()).orElseThrow();
            newCategory.setParent(parentCategory);
            newCategory.setLevel(parentCategory.getLevel() + 1);
        } else {
            newCategory.setLevel(1);
        }

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

    public List<CategoryDto> getCategoryTree(WorkGroup group) {
        List<CategoryDto> categories = getCategoryList(group);

        if (categories.isEmpty()) return categories;

        List<CategoryDto> topCategories = getRoot(categories);

        Map<Long, List<CategoryDto>> groupingByParent =
                groupingByParent(categories);

        addSubCategories(topCategories, groupingByParent);

        return topCategories;
    }

    public List<CategoryDto> getCategoryList(WorkGroup group) {
        return categoryRepository.findAllByGroup(group).stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    private List<CategoryDto> getRoot(List<CategoryDto> categories) {
        final int TOP_LEVEL = 1;

        return categories.stream()
                .filter(categoryDto -> categoryDto.getLevel() == TOP_LEVEL)
                .collect(Collectors.toList());
    }

    private Map<Long, List<CategoryDto>> groupingByParent(List<CategoryDto> categories) {
        return categories.stream()
                .collect(groupingBy(CategoryDto::getParentId));
    }

    private void addSubCategories(List<CategoryDto> parents, Map<Long, List<CategoryDto>> groupingByParent) {
        parents.forEach(parent -> {
            List<CategoryDto> subCategories = groupingByParent.get(parent.getCategoryId());

            if (subCategories == null) return;

            parent.setSubCategories(subCategories);

            addSubCategories(subCategories, groupingByParent);
        });
    }

    public void editCategory(List<CategoryDto> request, String groupUrl) {
        WorkGroup group = findGroupByUrl(groupUrl);
        List<CategoryDto> originalList = getCategoryList(group);

        IntStream.range(0, request.size())
                .forEach(i -> {
                    CategoryDto origin = originalList.get(i);
                    CategoryDto change = request.get(i);

                    changeInfoIfDifferent(originalList, origin, change);
                });

        //request 개수가 더 작으면 이후에 있는 origin은 다 삭제
        if (originalList.size() - 1 > request.size()) {
            IntStream.range(request.size(), originalList.size())
                    .forEach(i -> {
                        CategoryDto origin = originalList.get(i);
                        categoryRepository.deleteById(origin.getCategoryId());
                    });
        }
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
        if (change.getParentIdx() == null) return 0L;
        return originalList.get(change.getParentIdx()).getCategoryId();
    }

    private void changeName(Category origin, CategoryDto change) {
        origin.setName(change.getName());
        categoryRepository.save(origin);
    }

    private void changeParent(List<CategoryDto> originalList, Category origin, CategoryDto change) {
        Long parentId = getParentId(originalList, change);

        if (parentId == 0L) {
            origin.setParent(null);
            origin.setLevel(1);
            return;
        }

        Category parent = categoryRepository.findById(parentId).orElseThrow();
        origin.setParent(parent);
        origin.setLevel(change.getLevel());
        categoryRepository.save(origin);
    }

    @Transactional(readOnly = true)
    public Category getCategory(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseThrow();
    }
}
