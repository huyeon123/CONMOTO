package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.document.JoinRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JoinRequestRepository extends MongoRepository<JoinRequest, String> {
    Optional<JoinRequest> findByGroupUrl(String groupUrl);
}
