package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Category;
import com.huyeon.superspace.domain.board.dto.CategoryCreateDto;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.repository.NewCategoryRepository;
import com.huyeon.superspace.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewCategoryService {
    private final NewCategoryRepository categoryRepository;

    public CategoryDto getCategory(String id) {
        return findCategory(id).map(CategoryDto::new).orElseThrow();
    }

    private Optional<Category> findCategory(String id) {
        return categoryRepository.findById(id);
    }

    public String createCategory(CategoryCreateDto request) {
        Category category = new Category(request);

        if (Objects.nonNull(request.getParentId())) {
            Category parent = findCategory(request.getParentId()).orElseThrow();
            checkSameGroup(category, parent);
            category.setParentCategory(parent);
        }

        return categoryRepository.save(category).getId();
    }

    private void checkSameGroup(Category category, Category parent) {
        if (category.getGroupUrl().equals(parent.getGroupUrl())) return;
        throw new BadRequestException("잘못된 부모 카테고리 요청입니다!");
    }

    public List<CategoryDto> getCategoryTree(String groupUrl) {
        List<CategoryDto> categories = getCategoryList(groupUrl);

        if (categories.isEmpty()) return List.of();

        List<CategoryDto> topCategories = getTopCategories(categories);

        Map<String, List<CategoryDto>> groupingByParent = groupingByParent(categories);

        addSubCategories(topCategories, groupingByParent);

        return topCategories;
    }

    private List<CategoryDto> getTopCategories(List<CategoryDto> categories) {
        return categories.stream()
                .filter(category -> Objects.isNull(category.getParent()))
                .collect(toList());
    }

    //같은 상위 카테고리를 가지고 있는 노드끼리 그룹핑
    private Map<String, List<CategoryDto>> groupingByParent(List<CategoryDto> categories) {
        return categories.stream()
                .collect(groupingBy(category -> {
                    CategoryDto parent = category.getParent();
                    if (Objects.isNull(parent)) return "";
                    else return parent.getId();
                }));
    }

    private void addSubCategories(List<CategoryDto> parents, Map<String, List<CategoryDto>> groupingByParent) {
        parents.forEach(parent -> {
            List<CategoryDto> subCategories = groupingByParent.get(parent.getId());

            if (subCategories == null) return;

            parent.setChildren(subCategories);

            addSubCategories(subCategories, groupingByParent);
        });
    }

    public List<CategoryDto> getCategoryList(String groupUrl) {
        return categoryRepository.findAllByGroupUrl(groupUrl).stream()
                .map(CategoryDto::new)
                .collect(toList());
    }

    public void editCategory(List<CategoryDto> request, String groupUrl) {
        List<CategoryDto> originalList = getCategoryList(groupUrl);

        //수정과 생성을 동시에 할 수 없기 때문에, request가 original보다 클 수 없음.
        if (originalList.size() < request.size()) {
            throw new BadRequestException("기존 카테고리보다 더 많은 요청은 불가합니다");
        }

        IntStream.range(0, request.size())
                .forEach(i -> {
                    CategoryDto origin = originalList.get(i);
                    CategoryDto newElem = request.get(i);

                    change(origin, newElem);
                });

        categoryRepository.saveAll(convertDocList(originalList));

        if (originalList.size() > request.size()) {
            List<CategoryDto> deleteList = IntStream.range(request.size(), originalList.size())
                    .mapToObj(originalList::get)
                    .collect(toList());

            categoryRepository.deleteAll(convertDocList(deleteList));
        }
    }

    private void change(CategoryDto origin, CategoryDto change) {
        origin.setName(change.getName());
        origin.setParent(change.getParent());
    }

    private List<Category> convertDocList(List<CategoryDto> dtoList) {
        return dtoList.stream()
                .map(Category::new)
                .collect(toList());
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public void deleteAllByGroupUrl(String groupUrl) {
        categoryRepository.deleteAllByGroupUrl(groupUrl);
    }
}
