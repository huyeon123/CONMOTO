package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.FavoriteCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FavoriteCategoryRepository extends MongoRepository<FavoriteCategory, String> {
    Optional<FavoriteCategory> findAllByUserEmailAndGroupUrl(String email, String groupUrl);
}
