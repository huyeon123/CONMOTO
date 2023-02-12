package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Category;
import com.huyeon.superspace.domain.board.document.FavoriteCategory;
import com.huyeon.superspace.domain.board.dto.CategoryCreateDto;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.dto.FavoriteCategoryReq;
import com.huyeon.superspace.domain.board.repository.FavoriteCategoryRepository;
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
    private final FavoriteCategoryRepository favoriteRepository;

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

    public List<CategoryDto> getCategoryTree(String email, String groupUrl) {
        List<CategoryDto> categories = getCategoryList(groupUrl);

        checkFavorite(email, groupUrl, categories);

        if (categories.isEmpty()) return List.of();

        List<CategoryDto> topCategories = getTopCategories(categories);

        Map<String, List<CategoryDto>> groupingByParent = groupingByParent(categories);

        addSubCategories(topCategories, groupingByParent);

        return topCategories;
    }

    private void checkFavorite(String email, String groupUrl, List<CategoryDto> categories) {
        Optional<FavoriteCategory> favorites = findFavoriteByEmailAndUrl(email, groupUrl);

        if (favorites.isPresent()) {
            List<String> categoryIdList = favorites.get().getCategoryId();

            for (CategoryDto category : categories) {
                if (categoryIdList.contains(category.getId())) {
                    category.setFavorite(true);
                }
            }
        }
    }

    private Optional<FavoriteCategory> findFavoriteByEmailAndUrl(String email, String groupUrl) {
        return favoriteRepository.findAllByUserEmailAndGroupUrl(email, groupUrl);
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

    public void setFavorite(String email, FavoriteCategoryReq request) {
        if (request.isSet()) registerFavoriteCategory(email, request);
        else liftFavoriteCategory(email, request);
    }

    private void registerFavoriteCategory(String email, FavoriteCategoryReq request) {
        Optional<FavoriteCategory> optional = findFavoriteByEmailAndUrl(email, request.getGroupUrl());
        FavoriteCategory favorite;

        if (optional.isPresent()) {
            favorite = optional.get();
            List<String> categoryIdList = favorite.getCategoryId();
            categoryIdList.add(request.getCategoryId());
        } else {
            favorite = FavoriteCategory.builder()
                    .userEmail(email)
                    .groupUrl(request.getGroupUrl())
                    .categoryId(List.of(request.getCategoryId()))
                    .build();
        }

        favoriteRepository.save(favorite);
    }

    private void liftFavoriteCategory(String email, FavoriteCategoryReq request) {
        Optional<FavoriteCategory> optional = findFavoriteByEmailAndUrl(email, request.getGroupUrl());

        if (optional.isPresent()) {
            FavoriteCategory favorite = optional.get();
            List<String> categoryIdList = favorite.getCategoryId();
            categoryIdList.remove(request.getCategoryId());
            favoriteRepository.save(favorite);
        }
    }
}
