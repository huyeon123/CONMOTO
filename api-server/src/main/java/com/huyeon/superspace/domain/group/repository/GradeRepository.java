package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.document.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GradeRepository extends MongoRepository<Grade, String> {
    Optional<Grade> findByGroupUrl(String groupUrl);
}
