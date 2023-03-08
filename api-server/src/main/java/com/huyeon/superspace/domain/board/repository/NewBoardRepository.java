package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewBoardRepository extends MongoRepository<Board, Long> {
    @Query(value = "{id: {$lt: ?1}, groupUrl: ?0}", sort = "{id: -1}")
    List<Board> findNextLatestInGroup(
            String groupId,
            Long lastIndex,
            Pageable pageable
    );

    @Query(value = "{id: {$lt: ?1}, categoryId: ?0}", sort = "{id: -1}")
    List<Board> findNextLatestInCategory(
            String categoryId,
            Long lastIndex,
            Pageable pageable
    );

    @Query(value = "{id: {$lt: ?2}, author: ?0, groupUrl: ?1}", sort = "{id: -1}")
    List<Board> findNextLatestInUser(
            String email,
            String groupUrl,
            Long lastIndex,
            Pageable pageable
    );

    void deleteAllByGroupUrl(String groupurl);
}
