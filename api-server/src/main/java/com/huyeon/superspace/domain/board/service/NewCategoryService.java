package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Category;
import com.huyeon.superspace.domain.board.document.FavoriteCategory;
import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.dto.FavoriteCategoryReq;
import com.huyeon.superspace.domain.board.repository.FavoriteCategoryRepository;
import com.huyeon.superspace.domain.board.repository.NewCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewCategoryService {
    private final NewCategoryRepository categoryRepository;
    private final FavoriteCategoryRepository favoriteRepository;

    public CategoryDto getCategory(String id) {
        return new CategoryDto(findCategory(id));
    }

    public CategoryDto getCategoryByName(String groupUrl, String categoryName) {
        return categoryRepository.findByGroupUrlAndName(groupUrl, categoryName)
                .map(CategoryDto::new)
                .orElseThrow();
    }

    public CategoryDto createNotice(String groupUrl, String groupName) {
        Category notice = Category.builder()
                .groupUrl(groupUrl)
                .name("⭐공지사항")
                .description(groupName + "의 공지사항입니다.")
                .type(Category.Type.NOTICE)
                .fold(false)
                .indent(false)
                .availableWriteLevel(3) //그룹 생성시 최초 등급은 4단계임
                .build();

        notice = categoryRepository.save(notice);
        return new CategoryDto(notice);
    }

    private Category findCategory(String id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    public List<CategoryDto> getCategoryListByUrl(String groupUrl) {
        List<Category> categoryList = findCategoryListByUrl(groupUrl);
        return categoryList.stream()
                .map(CategoryDto::new)
                .collect(toList());
    }

    private List<Category> findCategoryListByUrl(String groupUrl) {
        return categoryRepository.findAllByGroupUrl(groupUrl);
    }

    public List<CategoryDto> getMyCategoryList(String userEmail, String groupUrl) {
        List<CategoryDto> categoryList = getCategoryList(groupUrl);
        checkFavorite(userEmail, groupUrl, categoryList);
        return categoryList;
    }

    private void checkFavorite(String email, String groupUrl, List<CategoryDto> categories) {
        FavoriteCategory favorites = findFavoriteByEmailAndUrl(email, groupUrl);

        List<String> categoryIdList = favorites.getCategoryIds();

        for (CategoryDto category : categories) {
            if (categoryIdList.contains(category.getId())) {
                category.setFavorite(true);
            }
        }
    }

    private FavoriteCategory findFavoriteByEmailAndUrl(String email, String groupUrl) {
        return favoriteRepository.findAllByUserEmailAndGroupUrl(email, groupUrl)
                .orElse(new FavoriteCategory(email, groupUrl));
    }

    public List<CategoryDto> getCategoryList(String groupUrl) {
        return categoryRepository.findAllByGroupUrl(groupUrl).stream()
                .map(CategoryDto::new)
                .collect(toList());
    }

    public void saveCategory(String groupUrl, List<CategoryDto> request) {
        List<Category> origin = findCategoryListByUrl(groupUrl);

        //기존 categoryId 덮어쓰기
        int min = Math.min(origin.size(), request.size());
        for (int i = 0; i < min; i++) {
            String originId = origin.get(i).getId();
            request.get(i).setId(originId);
        }

        //DTO -> Document
        List<Category> convert = request.stream()
                .map(Category::new)
                .collect(toList());

        categoryRepository.saveAll(convert);

        //기존 카테고리 수가 더 많았다면 넘치는 만큼 카테고리 제거
        if (origin.size() > request.size()) {
            List<Category> deleteTarget = new LinkedList<>();

            for (int i = request.size(); i < origin.size(); i++) {
                deleteTarget.add(origin.get(i));
            }

            categoryRepository.deleteAll(deleteTarget);
        }
    }

    public String editCategory(CategoryDto request) {
        Category origin = findCategory(request.getId());
        request.setId(origin.getId());
        return categoryRepository.save(new Category(request)).getId();
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
        FavoriteCategory favorites = findFavoriteByEmailAndUrl(email, request.getGroupUrl());

        List<String> categoryIdList = favorites.getCategoryIds();
        categoryIdList.add(request.getCategoryId());

        favoriteRepository.save(favorites);
    }

    private void liftFavoriteCategory(String email, FavoriteCategoryReq request) {
        FavoriteCategory favorites = findFavoriteByEmailAndUrl(email, request.getGroupUrl());

        List<String> categoryIdList = favorites.getCategoryIds();

        if (categoryIdList.contains(request.getCategoryId())) {
            categoryIdList.remove(request.getCategoryId());
            favoriteRepository.save(favorites);
        }
    }
}
