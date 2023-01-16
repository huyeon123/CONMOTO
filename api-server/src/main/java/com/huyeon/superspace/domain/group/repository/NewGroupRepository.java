package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.document.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewGroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findByUrl(String groupUrl);
    boolean existsByUrl(String groupUrl);
}
