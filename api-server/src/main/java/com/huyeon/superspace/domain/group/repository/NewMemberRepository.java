package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.document.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewMemberRepository extends MongoRepository<Member, String> {
    Optional<Member> findByGroupUrlAndUserEmail(String groupUrl, String userEmail);

    List<Member> findAllByUserEmail(String userEmail);

    List<Member> findAllByGroupUrl(String groupUrl);

    void deleteByGroupUrlAndUserEmail(String groupUrl, String userEmail);

    boolean existsByGroupUrlAndUserEmail(String groupUrl, String userEmail);
}
