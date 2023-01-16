package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.document.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NewMemberRepository extends MongoRepository<Member, String> {
    Optional<Member> findByGroupIdAndUserEmail(String groupId, String userEmail);

    List<Member> findAllByUserEmail(String userEmail);

    List<Member> findAllByGroupId(String groupId);

    void deleteByGroupIdAndUserEmail(String groupId, String userEmail);

    boolean existsByGroupIdAndUserEmail(String groupId, String userEmail);
}
