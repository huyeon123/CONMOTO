package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewContentRepository extends MongoRepository<Content, String> {
}
