package com.huyeon.superspace.domain.newboard.repository;

import com.huyeon.superspace.domain.newboard.document.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewContentRepository extends MongoRepository<Content, String> {
}
