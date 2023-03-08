package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.MemberLikePost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberLikePostRepository extends MongoRepository<MemberLikePost, String> {
}
