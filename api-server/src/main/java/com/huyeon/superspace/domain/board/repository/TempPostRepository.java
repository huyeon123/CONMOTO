package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.TempPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TempPostRepository extends MongoRepository<TempPost, String> {
    long countAllByAuthorAndGroupUrl(String email, String groupUrl);
    List<TempPost> findAllByAuthorAndGroupUrl(String email, String groupUrl);
}
