package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewBoardRepository extends MongoRepository<Board, String> {
    @Query(value = "{groupUrl: ?0, updatedAt: {$lt: ?1}}", sort = "{updatedAt: -1}")
    List<Board> findNextLatestInGroup(
            String groupId,
            LocalDateTime now,
            Pageable pageable
    );

    @Query(value = "{categoryName: ?0, updatedAt: {$lt: ?1}}", sort = "{updatedAt: -1}")
    List<Board> findNextLatestInCategory(
            String categoryId,
            LocalDateTime now,
            Pageable pageable
    );

    @Query(value = "{author: ?0, updatedAt: {$lt: ?1}}", sort = "{updatedAt: -1}")
    List<Board> findNextLatestInUser(
            String email,
            LocalDateTime now,
            Pageable pageable
    );

    void deleteAllByGroupUrl(String groupurl);
}
