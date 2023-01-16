package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewCategoryRepository extends MongoRepository<Category, String> {
    List<Category> findAllByGroupUrl(String groupUrl);

    void deleteAllByGroupUrl(String groupUrl);
}
