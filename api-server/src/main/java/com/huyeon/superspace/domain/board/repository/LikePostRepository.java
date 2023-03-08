package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.LikePost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LikePostRepository extends MongoRepository<LikePost, Long> {
}
